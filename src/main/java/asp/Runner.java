package asp;

import com.google.common.io.ByteStreams;
import fomatters.IParser;
import lombok.val;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Runner {

  private final static String TMP_DIR = "tmp/";
  private final static String TMP_MODEL_FILE = TMP_DIR + "model.lp";
  private final static String TMP_FACTS_FILE = TMP_DIR + "facts.lp";

  public Optional<List<OutputParser.Move>> run(IParser.ParsedInput input) throws IOException {
    createTmpDir();
    writeToTempModelFile();
    writeToTempFactsFile(input);

    val stdOut = exec(4);
    val reader = new BufferedReader(new InputStreamReader(stdOut));

    return new OutputParser(reader).parse();
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

  private InputStream exec(int horizon) throws IOException {
    val rt = Runtime.getRuntime();
    val gringoProc = rt.exec(getGringoCmd(horizon));
    val claspProc = rt.exec(getClaspCmd());

//    new BufferedReader(new InputStreamReader(gringoProc.getInputStream())).lines().forEach(System.err::println);

    InputStream gringoStdOut = gringoProc.getInputStream();
    OutputStream claspStdIn = claspProc.getOutputStream();
    ByteStreams.copy(gringoStdOut, claspStdIn);

    gringoStdOut.close();
    claspStdIn.close();

//    new BufferedReader(new InputStreamReader(claspProc.getInputStream())).lines().forEach(System.err::println);
    return claspProc.getInputStream();
  }

  private String getGringoCmd(int horizon) {
    return "gringo " + " " + TMP_FACTS_FILE + " " + TMP_MODEL_FILE + " -c horizon=" + horizon;
  }

  private String getClaspCmd() {
    return "clasp";
  }
}
