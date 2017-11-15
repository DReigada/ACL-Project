package smt;

import com.google.common.io.ByteStreams;
import fomatters.IParser;
import lombok.val;
import org.apache.commons.io.output.TeeOutputStream;
import sat.solver.AbstractSolver;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class Runner {
  private static final String SMT_INPUT_FILE = "tmp/input.smt";

  static Optional<Stream<AbstractSolver.Move>> run(IParser.ParsedInput input) throws IOException {
    // generate initial conditions
    val gen = new SmtFileGenerator(input);
    gen.generateAndSave(Paths.get(SMT_INPUT_FILE));


    // create process and start
    ProcessBuilder pb = new ProcessBuilder("/Users/dreigada/IST/ALC/labs/z3SMT/bin/z3", "-smt2", "-in");
    val process = pb.start();
//    val stdin = new TeeOutputStream(new BufferedOutputStream(process.getOutputStream()), new BufferedOutputStream(System.out));
    val stdin = new TeeOutputStream(new BufferedOutputStream(process.getOutputStream()), new BufferedOutputStream(new FileOutputStream("tmp/TEST.blaaa")));
    val stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

    // copy input file to stdin
    try (val inputFileContent = new FileInputStream(SMT_INPUT_FILE)) {
      ByteStreams.copy(inputFileContent, stdin);
      stdin.flush();
    }

    val parser = new OutputParser(gen.table, stdout);

    for (int currentMove = 0; currentMove < 10; currentMove++) {
      stdin.write(gen.generateStepConditions(gen.getStepTemplateFile(), currentMove).getBytes());
      stdin.flush();

      if (parser.isSat()) {
        for (int mov = 0; mov <= currentMove; mov++) {
          val eval = "(eval movement" + mov + ")\n";
          stdin.write(eval.getBytes());
        }

        stdin.flush();

        return Optional.of(parser.parseMoves(currentMove + 1));
      } else {
        stdin.write("(pop)\n".getBytes());
        stdin.flush();
      }
    }

    return Optional.empty();
  }


}
