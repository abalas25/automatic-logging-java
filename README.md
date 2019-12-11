# COURSE PROJECT - CS474 
**A program that adds logging automatically into Java programs.**

## Contributions
1. Ashwin Balasubramani - **leader**
2. Sachin Manimekalai Balakrishnan
3. Shreya Shrivastiva



## Files
- README.md 
- build.sbt 
- project 
- src
- target 
- CS474_project_documentation.docx

## File contents

**README.md** -  file contains all the basic necessary details about the files and folders which contain the homework and also the steps and documentation on how to install and run the code.

**build.sbt** - file contains the sbt dependencies of scala i.e., it contains all the library dependencies of the packages that was imported into coding the homework. All the dependencies where taken from the Maven Repository.

**project** - this folder contains the build.properties file has all the properties that is required to run a sbt program without errors. It also contains other files and folders relating to the scala version, sbt version, config classes and various streams.

**src** - this folder contains two other folders main and test.

The **main** folder,
1.	**_old_java_file_name.txt** – this file contains the original source code for which the instrumentation code is inserted and computed.
2.	**java_file_name.java** – this file contains the source code with the instrumentation lines for the Template class, which will invoke the template.scala file.
3.	**parser.scala** – this file has the code for all the parser implementations: reading the code, parsing it using AST parser, rewriting the AST with instrumentation nodes and unparsing the AST to get the file with instrumentation code.
4.	**template.scala** – this file contains the template class code which uses the instrumented java code to create two other files, one with the logging statements and another file with a table of all variables and their bindings in the application’s scope.
5.	**tracefile.txt** – file that contains the logging statements for the original java source code.
6.	**tablefile.txt** – file that contains all the variables and their bindings in the application’s scope.

The **test** folder,
1.	**launcher.scala** – the main file that launches all the implementation.

## Running the code

- Open IntelliJ IDEA and create a new project. 
- Select Scala from the left panel and sbt (sbt-based scala project) and click Next. 
- Give a name to the project and select the location where the project is to be created and also check for the Java and sbt versions and click Finish. 
- In the created project (can be found on the left panel), select the build.sbt file and copy all the dependencies from the build.sbt from this repository as it is. Check for the version for Scala and sbt before adding. 
- Create a new scala class under projectname -> src -> main -> scala. Copy the files parser.scala, template.scala and a java source code for input from the src - > main folder in this repository. 
- Similarly, in the test folder projectname -> src -> test -> scala. Copy the test files from the src - > test folder in this repository. 
- Save the project and Build the project. - Once the project is built successfully, click run the project.
- To run the code using ```sbt```, use the commands ```sbt clear compile run```.
