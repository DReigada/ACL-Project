package com.dreigada;

import conditions.Sat4JSolver;
import fomatters.InputParser;
import fomatters.OutputFormatter;
import lombok.val;

import java.io.File;

public class App {
  public static void main(String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Missing argument: input file");
      System.exit(1);
    }

    long startTime = System.currentTimeMillis();

    val file = new File(args[0]);
    val parsed = new InputParser(file).parse();

    val solver = new Sat4JSolver(parsed);

    val maxSteps = 20;
    val sol = solver.solve(maxSteps);

    if (sol.isPresent()) {
      val fmt = OutputFormatter.format(sol.get());
      System.out.println(fmt);
    } else {
      System.out.println("No solution for " + maxSteps + " steps.");
    }


    long endTime = System.currentTimeMillis();
    long duration = (endTime - startTime);

    System.err.println("Duration: " + duration);

  }

}
