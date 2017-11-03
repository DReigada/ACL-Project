package solver.externalSolvers;

import lombok.val;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class LingelingSolver implements IExternalSolver {

  private File executable;

  public LingelingSolver(String pathToExec) {
    this.executable = new File(pathToExec);
  }

  @Override
  public Optional<int[]> run(File file) throws IOException {
    val stdout = exec(file);
    val vars = filterLines(stdout);
    val arr = vars.toArray();

    if (arr.length == 0) {
      return Optional.empty();
    } else {
      return Optional.of(arr);
    }
  }

  private InputStream exec(File file) throws IOException {
    val rt = Runtime.getRuntime();
    val proc = rt.exec(getCmd(file));
    return proc.getInputStream();
  }

  private static IntStream filterLines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream))
        .lines()
        .filter(line -> line.startsWith("v "))
        .map(line -> line.substring(2))
        .flatMap(line -> Arrays.stream(line.split(" ")))
        .mapToInt(Integer::parseInt)
        .filter(a -> a != 0);
  }

  private String getCmd(File file) {
    return executable.getAbsolutePath() + " " + file.getAbsolutePath();
  }
}
