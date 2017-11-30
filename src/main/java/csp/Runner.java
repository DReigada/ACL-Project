package csp;

import com.google.common.io.ByteStreams;
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

  private final static String TMP_DIR = "tmp/";
  private final static String TMP_MODEL_FILE = TMP_DIR + "model.mzn";
  private final static String INPUT_DZN_FILE = TMP_DIR + "input.dzn";
  private final static String STEPS_DZN_FILE = TMP_DIR + "steps.dzn";

  public Runner(String binaryFile) {
    this.binaryFile = binaryFile;
  }

  public Optional<Stream<AbstractSolver.Move>> run(IParser.ParsedInput input) throws IOException {
    createTmpDir();

    val table = new Table(input);

    writeToTempModelFile();
    writeToInputFile(input, table);

    for (int steps = 0; steps <= 20; steps++) {
      val res = trySolve(steps, table);
      if (res.isPresent()) {
        return res;
      }
    }

    return Optional.empty();
  }

  private Optional<Stream<AbstractSolver.Move>> trySolve(int steps, Table table) throws IOException {
    writeToStepsFile(steps);
    val stdOut = exec();
    val reader = new BufferedReader(new InputStreamReader(stdOut));
    return new OutputParser(table, reader).parse();
  }

  private void writeToTempModelFile() throws IOException {
    val modelFileStream = getModelFile();
    val outputStream = new BufferedOutputStream(new FileOutputStream(TMP_MODEL_FILE));

    ByteStreams.copy(modelFileStream, outputStream);

    outputStream.close();
    modelFileStream.close();
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


  private InputStream exec() throws IOException {
    val rt = Runtime.getRuntime();
    val proc = rt.exec(getCmd());
//    new BufferedReader(new InputStreamReader(proc.getErrorStream())).lines().forEach(System.err::println);
    return proc.getInputStream();
  }

  private String getCmd() {
    return binaryFile + " " + TMP_MODEL_FILE + " " + INPUT_DZN_FILE + " " + STEPS_DZN_FILE;
  }

  private InputStream getModelFile() {
    return getClass().getResourceAsStream("/csp/model.mzn");
  }

  private static void createTmpDir() {
    new File(TMP_DIR).mkdir();
  }
}
