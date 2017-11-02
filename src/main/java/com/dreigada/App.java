package com.dreigada;

import conditions.AbstractSolver;
import conditions.ExternalSolverRunner;
import conditions.Sat4JSolver;
import conditions.externalSolvers.LingelingSolver;
import fomatters.IParser;
import fomatters.InputParser;
import fomatters.OutputFormatter;
import lombok.val;

import java.io.File;

public class App {
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println("Missing arguments");
      System.exit(1);
    }

    int timedOffset = 0;

    if (args[0].equals("--timed")) {
      timedOffset = 1;
    }

    val runner = runnner(args, timedOffset);

    if (timedOffset == 1) {
      timed(runner);
    } else {
      runner.run();
    }

  }

  private static Runnable runnner(String[] args, int timedOffset) {
    return () -> {
      try {
        val file = new File(args[timedOffset]);
        val input = new InputParser(file).parse();

        if (args.length == 2 + timedOffset) {
          runLingeling(input, args[1 + timedOffset]);
        } else {
          runSat4J(input);
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    };
  }

  private static void timed(Runnable f) {
    val startTime = System.currentTimeMillis();
    f.run();
    val endTime = System.currentTimeMillis();
    val duration = (endTime - startTime);

    System.err.println("Duration: " + duration);
  }

  private static void runLingeling(IParser.ParsedInput input, String pathToExec) {
    try {
      val lingeling = new LingelingSolver(pathToExec);
      val solver = new ExternalSolverRunner(input, "lingeling.input", lingeling);
      run(solver);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

  }

  private static void runSat4J(IParser.ParsedInput input) {
    try {
      val solver = new Sat4JSolver(input);
      run(solver);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void run(AbstractSolver solver) throws Exception {
    val maxSteps = 20;
    val sol = solver.solve(maxSteps);

    if (sol.isPresent()) {
      val fmt = OutputFormatter.format(sol.get());
      System.out.println(fmt);
    } else {
      System.out.println("No solution for " + maxSteps + " steps.");
    }
  }

}
