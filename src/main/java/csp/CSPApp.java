package csp;

import fomatters.InputParser;
import fomatters.OutputFormatter;
import lombok.val;

import java.io.File;
import java.util.stream.Collectors;

public class CSPApp {


  public static void main(String[] args) throws Exception {
    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/puzzles-students/puzzle-49.rr");
    val input = new InputParser(inputFile).parse();

    val startTime = System.currentTimeMillis();

    val res = new Runner("minizinc").run(input);

    val endTime = System.currentTimeMillis();

//    System.err.println("Duration: " + (endTime - startTime));

    if (res.isPresent()) {
      val output = OutputFormatter.format(res.get().collect(Collectors.toList()));
      System.out.println(output);
    } else {
      System.out.println("UNSAT");
    }
  }
}

