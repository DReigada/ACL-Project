package asp;

import com.google.common.io.ByteStreams;
import fomatters.IParser;
import lombok.val;
import sat.solver.AbstractSolver;

import java.io.*;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Runner {

  private final static String TMP_DIR = "tmp/";
  private final static String TMP_MODEL_FILE = TMP_DIR + "model.lp";
  private final static String TMP_FACTS_FILE = TMP_DIR + "facts.lp";


  public Optional<Stream<AbstractSolver.Move>> run(IParser.ParsedInput input) throws IOException {
    createTmpDir();
//    writeToTempModelFile();
    writeToTempFactsFile(input);

    return Optional.empty();
  }

  private void writeToTempModelFile() throws IOException {
    val modelFileStream = getModelFile();
    val outputStream = new BufferedOutputStream(new FileOutputStream(TMP_MODEL_FILE));

    ByteStreams.copy(modelFileStream, outputStream);

    outputStream.close();
    modelFileStream.close();
  }

  private static void writeToTempFactsFile(IParser.ParsedInput input) throws IOException {
    val lines = FactsGenerator.generate(input);
    val outputStream = new BufferedOutputStream(new FileOutputStream(TMP_FACTS_FILE));

    outputStream.write(lines.collect(Collectors.joining("\n")).getBytes());
    outputStream.close();
  }

  private InputStream getModelFile() {
    return getClass().getResourceAsStream("/asp/model.lp");
  }


  private static void createTmpDir() {
    new File(TMP_DIR).mkdir();
  }
}
