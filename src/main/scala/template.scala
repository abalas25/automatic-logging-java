import java.io.{File, PrintWriter}
import java.util
import scala.util.control._


object template extends App{

  val loop = new Breaks
  class ArgRec {
    var argName: String = null
    val argVal = new util.ArrayList[argVal]

    def getArgVal: util.ArrayList[argVal] = argVal

    def addArgVal(argVal: argVal): Unit = {
      this.argVal.add(argVal)
    }

    def getArgName: String = argName

    def setArgName(argName: String): Unit = {
      this.argName = argName
    }
  }

  class argVal(var linePosition: Int, var TOS: String, var values: String) {
    def getlinePosition: Int = linePosition

    def getTOS: String = TOS

    def getValues: String = values

  }

  var completeLogging = ""
  var arg: util.ArrayList[ArgRec] = _

  //Changes String to Seq[String] inorder to make Java-scala compatibility
  def pair(a: String, b:String): Seq[String] ={
    Seq(a + ", " + b)
  }

  //Instrum takes 2 finite parameters (The line position of the particular statement/loop), the type of the statement, and finally the variables (which are taken as Sequence of String)
  def instrum (linePosition: Int, TOS: String, args: String* ) : Unit = {
    completeLogging += "\nLine Number: " + linePosition + " Type of statement: " + TOS + " Parameters: "
    //Print the instrumented statement in a file (File created below)
    val arg_len: Int = args.length
    var i: Int = 0
    while (i < arg_len) {

      //args is Seq[String]. Hence, For eg. If the full string is ** "AddTwoNumbers.main().sum: ", String.valueOf(sum) ** then the string is separated using comma

      completeLogging += args(i).split(",")(0) //The first part of the Seq[String]
      completeLogging += args(i).split(",")(1) //The second part of the Seq[String]
      traceTable(linePosition, TOS, args(i))
      i += 2
    }
  }


  def logging(): Unit = {
    val trace = new File("/Users/ashwinbalasubramani/IdeaProjects/project_cs474/TraceFile.txt") //creating file TraceFile
    new PrintWriter(trace) { write(completeLogging); close() } //writing to the trace
    printToFile // calling the method printToFile that outputs trace into file
  }

  def printToFile: PrintWriter = {

    //Printing the table that consists of
    var TraceInput: String = ""
    arg.forEach{args: ArgRec =>
      TraceInput += "\n        " + args.getArgName + "           "
      args.getArgVal.forEach{argvalues: argVal =>
        TraceInput += "\nPresent at Line Position: " + argvalues.getlinePosition
        TraceInput += "\nType of Statement: " + argvalues.getTOS
        TraceInput += "\nValue: " + argvalues.getValues
      }
    }

    val trace1 = new File("tableFile.txt")
    new PrintWriter(trace1) { write(TraceInput); close()}
  }

  //construct a table of all variables and their bindings in the application's scopes, and this table will include the path to a variable, its declaration in the line of the code, and each variable will be assigned a unique identifier.
  def traceTable(linePosition: Int, TOS: String, argument: String): AnyVal = {
    val argVal: argVal = new argVal(linePosition, TOS, argument.split(",")(1)) //second part of the string is the value
    if(arg == null) {
      val args: ArgRec = new ArgRec
      args.setArgName(argument.split(",")(0)) // name of the variable is the first part of the string
      args.addArgVal(argVal)
      arg = new util.ArrayList[ArgRec]
      arg.add(args) // adding the values to array
    }
    else {
      var counter: Int = 0
      arg.forEach{allarg: ArgRec =>
        if(allarg.getArgName == argument) {
          allarg.addArgVal(argVal)
          counter += 1
        }
      }

      if(counter == 0) {
        val args: ArgRec = new ArgRec
        args.setArgName(argument.split(",")(0)) //first part of the string
        args.addArgVal(argVal)
        arg.add(args)
      }
    }
  }
}
