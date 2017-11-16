package smt;

import com.google.common.io.ByteStreams;
import fomatters.IParser;
import lombok.val;
import org.apache.commons.io.output.TeeOutputStream;
import sat.solver.AbstractSolver;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;
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

    return bla(stdin, gen, parser);

//    for (int currentMove = 0; currentMove < 10; currentMove++) {
//      stdin.write(gen.generateStepConditions(gen.getStepTemplateFile(), currentMove).getBytes());
//      stdin.flush();
//
//      if (parser.isSat()) {
//        for (int mov = 0; mov <= currentMove; mov++) {
//          val eval = "(eval movement" + mov + ")\n";
//          stdin.write(eval.getBytes());
//        }
//
//        stdin.flush();
//
//        return Optional.of(parser.parseMoves(currentMove + 1));
//      } else {
//        stdin.write("(pop)\n".getBytes());
//        stdin.flush();
//      }
//    }
//
//    return Optional.empty();
  }


  private static Optional<Stream<AbstractSolver.Move>> bla(OutputStream stdin, SmtFileGenerator gen, OutputParser parser) throws IOException {
    int[] arr = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 16, 20};
    int upperLimit = arr[arr.length - 1];
    int bottomLimit = arr[0];

    for (int step : arr) {
      for (int currentMove = bottomLimit; currentMove <= step; currentMove++) {
        stdin.write(gen.generateStepConditions(gen.getStepTemplateFile(), currentMove).getBytes());
      }
      stdin.flush();


      val solOpt = verifyObjective(step + 1, parser, stdin, gen);
      if (solOpt.isPresent()) {
        upperLimit = step;
        val moves = solOpt.get();
        if (upperLimit == bottomLimit) {
          return Optional.of(moves);
        } else {
          return Optional.of(traceBack(upperLimit - 1, moves, parser, stdin, gen));
        }
      } else {
        bottomLimit = step + 1;
      }
    }

    return Optional.empty();
  }

  private static void doPop(OutputStream stdin) throws IOException {
    stdin.write("(pop)\n".getBytes());
    stdin.flush();
  }

  private static Stream<AbstractSolver.Move> traceBack(int step, Stream<AbstractSolver.Move> old, OutputParser parser, OutputStream stdin, SmtFileGenerator gen) throws IOException {
    doPop(stdin);
    val solOpt = verifyObjective(step + 1, parser, stdin, gen);
    if (solOpt.isPresent()) {
      return traceBack(step - 1, solOpt.get(), parser, stdin, gen);
    } else {
      return old;
    }
  }


  private static Optional<Stream<AbstractSolver.Move>> verifyObjective(int steps, OutputParser parser, OutputStream stdin, SmtFileGenerator gen) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(gen.getTemplateYADAYADA()))) {
      String lines = reader.lines().map(a -> gen.replaceIfMatchesObjective(a, steps)).collect(Collectors.joining("\n", "\n", "\n"));
      stdin.write(lines.getBytes());
      stdin.flush();
    }

    if (parser.isSat()) {
      val moves = getMoves(steps, parser, stdin);
      doPop(stdin);
      return Optional.of(moves);
    } else {
      doPop(stdin);
      return Optional.empty();
    }
  }

  private static Stream<AbstractSolver.Move> getMoves(int steps, OutputParser parser, OutputStream stdin) throws IOException {
    for (int mov = 0; mov < steps; mov++) {
      val evalOrig = "(eval movementOrig" + mov + ")\n";
      val evalDest = "(eval movementDest" + mov + ")\n";
      val evalRobot = "(eval movementRobot" + mov + ")\n";

      stdin.write(evalOrig.getBytes());
      stdin.write(evalDest.getBytes());
      stdin.write(evalRobot.getBytes());
    }

    stdin.flush();

    return parser.parseMoves(steps);
  }


}
