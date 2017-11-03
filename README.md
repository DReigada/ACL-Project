# ACL-Project1
 Algorithms for Computational Logic Project part 1
 
 ### Description
 The project was implemented in Java and the chosen solving method was SAT.
 Features:
  - Supports both Sat4J and Lingeling as the SAT solvers. (Lingeling is much faster for big problems whilst Sat4j is faster for smaller ones;
  - Allows timing the execution with the command "--time" before all the arguments.

 ### To compile (the project and lingeling):
 ./compile.sh
 
 ### To run using the lingeling compiled with the compile script:
 ./proj1.sh inputFile > outputFile
 
 ### To run using another lingeling binary: 
 java -jar proj1.jar inputFile pathToBinary > outputFile
 
 ### To run using Sat4J (slower for bigger problems)
 java -jar proj1.jar inputFile > outputFile
