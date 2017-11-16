package smt;

import fomatters.InputParser;
import fomatters.OutputFormatter;
import lombok.val;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SmtApp {

  public static void main(String[] args) throws Exception {
    new File("tmp").mkdir();

    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/small_puzzle/small_3.rr");
    val input = new InputParser(inputFile).parse();

    val startTime = System.currentTimeMillis();

    val res = Runner.run(input);

    val endTime = System.currentTimeMillis();
    val duration = (endTime - startTime);

    System.err.println("Duration: " + duration);

    if (res.isPresent()) {
      val out = OutputFormatter.format(res.get().collect(Collectors.toList()));

      System.out.println(out);
    } else {
      System.out.println("UNSAT");
      System.exit(1);
    }

  }


  public static String generateConditions(File filePath, int time) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      Stream<String> newLines =
          reader.lines()
              .map(a -> replace(a, time, time + 1));

      return newLines.collect(Collectors.joining("\n"));
    }
  }

  private static String replace(String str, int t0, int t1) {
    return str
        .replace("$0", Integer.toString(t0))
        .replace("$1", Integer.toString(t1));
  }

}
