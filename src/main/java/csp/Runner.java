package csp;

import fomatters.IParser;
import lombok.val;
import sat.solver.AbstractSolver;
import table.Table;

import java.io.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Runner {
  private final String binaryFile;
  private final static String INPUT_DZN_FILE = "input.dzn";
  private final static String STEPS_DZN_FILE = "steps.dzn";

  public Runner(String binaryFile) {
    this.binaryFile = binaryFile;
  }

  public Optional<Stream<AbstractSolver.Move>> run(IParser.ParsedInput input) throws IOException {
    val table = new Table(input);

    writeToInputFile(input, table);

    writeToStepsFile(5);

    return Optional.empty();
  }

  private static void writeToStepsFile(int steps) throws IOException {
    val outputStream = new BufferedOutputStream(new FileOutputStream(STEPS_DZN_FILE));
    val line = "maxSteps = " + steps + ";\n";

    outputStream.write(line.getBytes());
    outputStream.close();
  }


  private static void writeToInputFile(IParser.ParsedInput input, Table table) throws IOException {
    val lines = new DZNGenerator().getDZN(input, table);
    val outputStream = new BufferedOutputStream(new FileOutputStream(INPUT_DZN_FILE));

    outputStream.write(lines.collect(Collectors.joining()).getBytes());
    outputStream.close();
  }


  private InputStream exec(File file) throws IOException {
    val rt = Runtime.getRuntime();
    val proc = rt.exec(getCmd(file));
    return proc.getInputStream();
  }

  private String getCmd(File file) {
    return binaryFile + " " + file.getAbsolutePath();
  }
}
