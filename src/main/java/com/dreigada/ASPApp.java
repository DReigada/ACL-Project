package asp;

import fomatters.InputParser;
import lombok.val;

import java.io.File;

public class ASPApp {
  public static void main(String[] args) throws Exception {

    val inputFile = new File(args[0]);
//    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/tests/r8_25_12.rr");
    val input = new InputParser(inputFile).parse();

    val startTime = System.currentTimeMillis();

    val moves = new Runner().run(input);

    val endTime = System.currentTimeMillis();

    System.err.println("Duration: " + (endTime - startTime));

    if (moves.isPresent()) {
      val output = OutputFormatter.format(moves.get());
      System.out.println(output);
    } else {
      System.out.println("UNSAT");
    }
  }

}
