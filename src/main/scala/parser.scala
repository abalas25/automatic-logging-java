
import java.io.{BufferedReader, File, FileInputStream, FileOutputStream, FileReader}
import java.util

import scala.util.control._
import org.eclipse.jdt.core.dom.rewrite.{ASTRewrite, ListRewrite}
import org.eclipse.jdt.core.dom.{AST, ASTNode, ASTParser, ASTVisitor, AssertStatement, Assignment, Block, CompilationUnit, ExpressionStatement, ForStatement, IfStatement, MethodDeclaration, MethodInvocation, ReturnStatement, SimpleName, Statement, SwitchStatement, TextElement, TypeDeclaration, VariableDeclarationStatement, WhileStatement}
import org.eclipse.jface.text.{Document, IDocument}
import org.eclipse.text.edits.TextEdit
import org.apache.commons.io.FileUtils

import scala.collection.immutable.LazyList

class parser  {
  // original java source path
  val file = new File("/Users/ashwinbalasubramani/IdeaProjects/project_cs474/src/main/scala/AddTwoNumbers.java")
  val src = new FileInputStream(file).getChannel
  val dest = new FileOutputStream(new File("/Users/ashwinbalasubramani/IdeaProjects/project_cs474/src/main/scala/_old_AddTwoNumbers.txt")).getChannel
  dest.transferFrom(src,0,src.size())

  // the original file is copied to a new text file, so that the rewritten code can be replaced by the original source code
  val br = new BufferedReader(new FileReader(file))
  val theString = LazyList.continually(br.readLine()).takeWhile(_ != null).mkString("\n")

  //Converting the string into sequence of characters and storing it in an array,followed by parsing
  val source: Array[Char] = theString.toCharArray
  val parser = ASTParser.newParser(AST.JLS11)
  parser.setKind(ASTParser.K_COMPILATION_UNIT)
  parser.setSource(source)
  parser.setResolveBindings(true)
  val result = parser.createAST(null).asInstanceOf[CompilationUnit]
  val all = new util.ArrayList[Statement]
  val loop = new Breaks

  //If expression is present, it will be added to the node.
  class exp extends ASTVisitor {
    // expression
    val expression = new util.ArrayList[ExpressionStatement]
    override def visit(node: ExpressionStatement): Boolean = {
      expression.add(node)
      super.visit(node)
    }
    def getExpression: util.ArrayList[ExpressionStatement] = expression
  }

  val ex = new exp
  result.accept(ex)
  ex.getExpression.forEach{e =>
    all.add(e)
  }

  //If return statement is present, it will be added to the node.
  class ret extends ASTVisitor {
    // return statement
    val returnStatement = new util.ArrayList[ReturnStatement]
    override def visit(node: ReturnStatement): Boolean = {
      returnStatement.add(node)
      super.visit(node)
    }
    def getReturn: util.ArrayList[ReturnStatement] = returnStatement
  }

  val r = new ret
  result.accept(r)
  r.getReturn.forEach{e =>
    all.add(e)
  }

  //If while statements are present, they will be added to the node.
  class whileState extends ASTVisitor{
    // while statement
    val whileStatement = new util.ArrayList[WhileStatement]
    override def visit(node: WhileStatement): Boolean = {
      whileStatement.add(node)
      super.visit(node)
    }
    def getWhile: util.ArrayList[WhileStatement] = whileStatement
  }

  val whi = new whileState
  result.accept(whi)
  whi.getWhile.forEach{e =>
    all.add(e)
  }

  //If switch statements are present, they will be added to the node.
  class Switching extends ASTVisitor {
    // switch statement
    val switchStatement = new util.ArrayList[SwitchStatement]

    override def visit(node: SwitchStatement): Boolean = {
      switchStatement.add(node)
      super.visit(node)
    }

    def getSwitch: util.ArrayList[SwitchStatement] = switchStatement
  }
  val switches = new Switching
  result.accept(switches)
  switches.getSwitch.forEach{e =>
    all.add(e)
  }

  //The statements contatining if are present, they will be added to the node.
  class IF extends ASTVisitor {
    val ifStatement = new util.ArrayList[IfStatement]
    override def visit(node: IfStatement): Boolean = {
      ifStatement.add(node)
      super.visit(node)
    }
    def getIF: util.ArrayList[IfStatement] = ifStatement
  }
  val iffy = new IF
  result.accept(iffy)
  iffy.getIF.forEach{e =>
    all.add(e)
  }

  //If for statements are present, they will be added to the node.
  class FOR extends ASTVisitor {
    // for statement
    val forStatement = new util.ArrayList[ForStatement]
    override def visit(node: ForStatement): Boolean = {
      forStatement.add(node)
      super.visit(node)
    }
    def getFor: util.ArrayList[ForStatement] = forStatement
  }
  val fr = new FOR
  result.accept(fr)
  fr.getFor.forEach{e =>
    all.add(e)
  }

  //If methods are invocated, they will be added
  class minvoc extends ASTVisitor {
    // method invocation statement
    val methodInvocation = new util.ArrayList[MethodInvocation]
    override def visit(node: MethodInvocation): Boolean = {
      methodInvocation.add(node)
      super.visit(node)
    }
    def getInvoc: util.ArrayList[MethodInvocation] = methodInvocation
  }
  val mi = new minvoc
  result.accept(mi)

  class sName extends ASTVisitor {
    // simple name
    val simpleName = new util.ArrayList[SimpleName]
    override def visit(node: SimpleName): Boolean = {
      simpleName.add(node)
      super.visit(node)
    }
    def getSName: util.ArrayList[SimpleName] = simpleName
  }
  val sname = new sName
  result.accept(sname)

  // adding new nodes using AST rewrite
  val rewriter = ASTRewrite.create(result.getAST)
  val scope: Block = result.getAST.newBlock()

  all.forEach{state: Statement =>
    val lineNumber: Int = result.getLineNumber(state.getStartPosition)
    var c: Int = 0
    //Once it gets the lin number, it wil check the which method is present
    sname.getSName.forEach{sn: SimpleName =>
      if(result.getLineNumber(sn.getStartPosition) == lineNumber) {
        c += 1
        mi.getInvoc.forEach{meth: MethodInvocation =>
          if(result.getLineNumber(meth.getStartPosition) == lineNumber && sn.getIdentifier.equals(meth.getName.toString)) {
            c -= 1

          }
        }
      }
    }

    //identifying the type of statements
    if(c > 0) {
      var template: String = "template.instrum(" + result.getLineNumber(state.getStartPosition) + ""
      val clas: String = state.getClass.getName
      if(clas == "org.eclipse.jdt.core.dom.ReturnStatement") {
        template = template + ", \"Return Statement\""
      } else if(clas == "org.eclipse.jdt.core.dom.ExpressionStatement") {
        template = template + ", \"Assignment Statement\""
      } else if(clas == "org.eclipse.jdt.core.dom.IfStatement") {
        template = template + ", \"If Statement\""
      } else if(clas == "org.eclipse.jdt.core.dom.ForStatement") {
        template = template + ", \"For Statement\""
      } else if(clas == "org.eclipse.jdt.core.dom.SwitchStatement") {
        template = template + ", \"Switch Statement\""
      } else if(clas == "org.eclipse.jdt.core.dom.WhileStatement") {
        template = template + ", \"While Statement\""
      }

      var check: Int = 0
      var count: Int = 0
      var state1: ASTNode = state.getParent
      var state2: String = state1.getClass.getName

      while(state2 != "org.eclipse.jdt.core.dom.MethodDeclaration") {
        if(state2.equals("org.eclipse.jdt.core.dom.TypeDeclaration")) {
          check = 1

        }
        state1 = state1.getParent
        state2 = state1.getClass.getName
      }

      // to identify the method declarations and type declarations to capture the value of each expression/statement
      // this block executes when the statement is under a method block
        if(check == 0) {
        val md = state1.asInstanceOf[MethodDeclaration]
        val td = md.getParent.asInstanceOf[TypeDeclaration]

          // simple name contains all the varibales used in the code
          sname.getSName.forEach{sn: SimpleName =>
          if(result.getLineNumber(sn.getStartPosition) == lineNumber) {
            count = 0
            mi.getInvoc.forEach{meth: MethodInvocation =>
              if(result.getLineNumber(meth.getStartPosition) == lineNumber && sn.getIdentifier.equals(meth.getName.toString)) {
                count = 1

              }
            }

            if(count == 0) {
              // here the instrumentation code is built by using the line number, class name, its method name and variable names along with its values
              template = template  + ", template.pair( \"" + td.getName + "." + md.getName + "()." + sn.getIdentifier + ": \"," + " String.valueOf(" + sn.getIdentifier + "))"
            }
          }
        }
      }

      else {
          // if the statement is not present under a method this block executes
        val td = state1.isInstanceOf[TypeDeclaration]
        sname.getSName.forEach{sn: SimpleName =>
          if(result.getLineNumber(sn.getStartPosition) == lineNumber) {
            count = 0
            mi.getInvoc.forEach{meth: MethodInvocation =>
              if(result.getLineNumber(meth.getStartPosition) == lineNumber && sn.getIdentifier.equals(meth.getName.toString)) {
                count = 1

              }
            }
            // similar to the previous block, here the instrumentation code is built as before, but without the method name
            if(count == 0) template = template + ", \"" + td.getClass.getName + "." + " String.valueOf(" + sn.getIdentifier+ ")"
          }
        }
      }
      // a new text element is created to insert the constructd instrumentation line into the AST
      template = template + ");"
      val text: TextElement = result.getAST.newTextElement
      text.setText(template)

      val text1: TextElement = result.getAST.newTextElement
      text1.setText("template.printTrace();")

      // to get the parent of each node and rewrite it with an element from the list to the AST
      val list1: ListRewrite = rewriter.getListRewrite(state.getParent, Block.STATEMENTS_PROPERTY)

      // if it is a looping statement or return statement, the instrumentation code is inserted before because sometimes if the these statements
      // are added after the original statement, it may become unreachable
      if(clas.equals("org.eclipse.jdt.core.dom.IfStatement") || clas.equals("org.eclipse.jdt.core.dom.ForStatement") || clas.equals("org.eclipse.jdt.core.dom.WhileStatement") || clas.equals("org.eclipse.jdt.core.dom.SwitchStatement") || clas.equals("org.eclipse.jdt.core.dom.ReturnStatement")) {
        list1.insertBefore(text, state, null)
      }
      else {
        // all other kinds of statements are added after the original line
        list1.insertAfter(text, state, null)
      }
    }
  }
  // to modify the AST, rewrite it and unparse it, the output object must be converted into an IDocument
  val doc: IDocument = new Document(FileUtils.readFileToString(new File(file.getAbsolutePath), "UTF-8"))
  //rewriting
  val edits: TextEdit = rewriter.rewriteAST(doc, null)
  edits.apply(doc)
  // edits made are applied here
  val getValue: String = doc.get
  // the new file with the instrumentation code is written into the old file
  FileUtils.writeStringToFile(new File("/Users/ashwinbalasubramani/IdeaProjects/project_cs474/src/main/scala/AddTwoNumbers.java"), getValue, "UTF-8")
}