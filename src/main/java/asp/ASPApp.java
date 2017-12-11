package asp;

import fomatters.InputParser;
import lombok.val;

import java.io.File;

public class ASPApp {
  public static void main(String[] args) throws Exception {

//    val inputFile = new File(args[0]);
    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/small_puzzle/small_1.rr");
    val input = new InputParser(inputFile).parse();

    new Runner().run(input);

  }

}
