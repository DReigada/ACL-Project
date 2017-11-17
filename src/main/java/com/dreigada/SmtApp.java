package com.dreigada;

import fomatters.InputParser;
import fomatters.OutputFormatter;
import lombok.val;
import smt.Runner;

import java.io.File;
import java.util.stream.Collectors;

public class SmtApp {

  public static void main(String[] args) throws Exception {
    new File("tmp").mkdir();

    val inputFile = new File(args[0]);
    val input = new InputParser(inputFile).parse();

    val startTime = System.currentTimeMillis();

    val res = new Runner(args[1]).run(input);

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
