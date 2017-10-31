package com.dreigada;

import conditions.Solver;
import fomatters.InputParser;
import fomatters.OutputFormatter;
import lombok.val;

public class App {
  public static void main(String[] args) throws Exception {
    val file = "/Users/dreigada/Downloads/small_puzzle/small_3.rr";

    val parsed = new InputParser(file).parse();

    val solver = new Solver(parsed);

    val maxSteps = 20;
    val sol = solver.solve(maxSteps);

    if (sol.isPresent()) {
      val fmt = OutputFormatter.format(sol.get());
      System.out.println(fmt);
    } else {
      System.out.println("Not solution for " + maxSteps + " steps.");
    }
  }

}
