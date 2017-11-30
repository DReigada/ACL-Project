package csp;

import fomatters.InputParser;
import lombok.val;

import java.io.File;

public class CSPApp {


  public static void main(String[] args) throws Exception {
    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/puzzles-students/puzzle-54.rr");
    val input = new InputParser(inputFile).parse();

    new Runner("").run(input);
  }
}

