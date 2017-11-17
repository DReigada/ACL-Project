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
  private static final String SMT_INITIAL_INPUT_FILE = "tmp/initialInput.smt";
  private static final int MAX_STEPS = 20;
  private String executablePath;

  public Runner(String executablePath) {
    this.executablePath = executablePath;
  }


  public Optional<Stream<AbstractSolver.Move>> run(IParser.ParsedInput input) throws IOException {
    // generate initial conditions
    val gen = new SmtFileGenerator(input);
    gen.generateAndSave(Paths.get(SMT_INITIAL_INPUT_FILE));

    // create process and start
    ProcessBuilder pb = new ProcessBuilder(executablePath, "-smt2", "-in");
    val process = pb.start();
    val stdin = new TeeOutputStream(new BufferedOutputStream(process.getOutputStream()), new BufferedOutputStream(new FileOutputStream("tmp/TEST.blaaa")));
    val stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

    // copy input file to stdin
    try (val inputFileContent = new FileInputStream(SMT_INITIAL_INPUT_FILE)) {
      ByteStreams.copy(inputFileContent, stdin);
      stdin.flush();
    }

    val outputParser = new OutputParser(gen.table, stdout);

    return doRun(gen, stdin, outputParser);
  }

  protected Optional<Stream<AbstractSolver.Move>> doRun(SmtFileGenerator gen, OutputStream stdin, OutputParser outputParser) throws IOException {
    if (verifyObjective(0, outputParser, stdin, gen).isPresent()) {
      return Optional.of(Stream.empty());
    }

    for (int step = 0; step < MAX_STEPS; step++) {
      stdin.write(gen.generateStepConditions(gen.getStepTemplateFile(), step).getBytes());
      stdin.flush();

      val solOpt = verifyObjective(step + 1, outputParser, stdin, gen);
      if (solOpt.isPresent()) {
        val moves = solOpt.get();
        return Optional.of(moves);
      }
    }

    return Optional.empty();
  }


  protected void doPop(OutputStream stdin) throws IOException {
    stdin.write("(pop)\n".getBytes());
    stdin.flush();
  }


  protected Optional<Stream<AbstractSolver.Move>> verifyObjective(int steps, OutputParser parser, OutputStream stdin, SmtFileGenerator gen) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(gen.getCheckStepTemplate()))) {
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

  private Stream<AbstractSolver.Move> getMoves(int steps, OutputParser parser, OutputStream stdin) throws IOException {
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
