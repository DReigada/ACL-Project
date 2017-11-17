package smt;

import lombok.val;
import sat.solver.AbstractSolver;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

public class RunnerCustomIncrement extends Runner {

  public RunnerCustomIncrement(String executablePath) {
    super(executablePath);
  }

  @Override
  protected Optional<Stream<AbstractSolver.Move>> doRun(SmtFileGenerator gen, OutputStream stdin, OutputParser outputParser) throws IOException {
    int[] arr = new int[]{0, 1, 2, 4, 8, 16, 20};
    int upperLimit = arr[arr.length - 1];
    int bottomLimit = arr[0];

    if (verifyObjective(0, outputParser, stdin, gen).isPresent()) {
      return Optional.of(Stream.empty());
    }

    for (int step : arr) {
      for (int currentMove = bottomLimit; currentMove <= step; currentMove++) {
        stdin.write(gen.generateStepConditions(gen.getStepTemplateFile(), currentMove).getBytes());
      }
      stdin.flush();


      val solOpt = verifyObjective(step + 1, outputParser, stdin, gen);
      if (solOpt.isPresent()) {
        upperLimit = step;
        val moves = solOpt.get();
        if (upperLimit == bottomLimit) {
          return Optional.of(moves);
        } else {
          return Optional.of(traceBack(upperLimit - 1, moves, outputParser, stdin, gen));
        }
      } else {
        bottomLimit = step + 1;
      }
    }

    return Optional.empty();
  }

  private Stream<AbstractSolver.Move> traceBack(int step, Stream<AbstractSolver.Move> old, OutputParser parser, OutputStream stdin, SmtFileGenerator gen) throws IOException {
    doPop(stdin);
    val solOpt = verifyObjective(step + 1, parser, stdin, gen);
    if (solOpt.isPresent()) {
      return traceBack(step - 1, solOpt.get(), parser, stdin, gen);
    } else {
      return old;
    }
  }
}
