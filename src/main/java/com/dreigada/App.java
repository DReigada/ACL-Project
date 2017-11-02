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
    if (args.length < 1) {
      System.out.println("Missing argument: input file");
      System.exit(1);
    }

    val file = new File(args[0]);
    val input = new InputParser(file).parse();

    time("Sat4J", () -> runSat4J(input));
    time("Lingeling", () -> runLingeling(input, "/Users/dreigada/IST/ALC/labs/lingeling/lingeling"));
  }

  private static void time(String nameOpt, Runnable f) {
    val startTime = System.currentTimeMillis();
    f.run();
    val endTime = System.currentTimeMillis();
    val duration = (endTime - startTime);

    String maybeName = "";
    if (!nameOpt.isEmpty()) {
      maybeName = " [" + nameOpt + "]";
    }
    System.err.println("Duration" + maybeName + ": " + duration);
  }

  private static void time(Runnable f) {
    time("", f);
  }

  private static void runLingeling(IParser.ParsedInput input, String pathToExec) {
    try {
      val lingeling = new LingelingSolver(pathToExec);
      val solver = new ExternalSolverRunner(input, "bla.out", lingeling);
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
