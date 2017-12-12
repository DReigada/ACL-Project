package asp;

import fomatters.InputParser;
import lombok.val;

import java.io.File;

public class ASPApp {
  public static void main(String[] args) throws Exception {

//    val inputFile = new File(args[0]);
    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/small_puzzle/small_2.rr");
    val input = new InputParser(inputFile).parse();

    val moves = new Runner().run(input);

    if (moves.isPresent()) {
      val output = OutputFormatter.format(moves.get());
      System.out.println(output);
    } else {
      System.out.println("UNSAT");
    }
  }

}
