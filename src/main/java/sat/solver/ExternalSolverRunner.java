package sat.solver;

import com.google.common.io.ByteStreams;
import sat.solver.externalSolvers.IExternalSolver;
import fomatters.IParser;
import lombok.val;
import sat.variables.ClauseFormula;
import sat.variables.VarClause;
import sat.variables.VarMap;

import java.io.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class ExternalSolverRunner extends AbstractSolver {
  private final String LINE_DELIMITER = " 0\n";
  private final Writer writer;
  private final AtomicInteger lineCount;
  private final File tmpFile;
  private final IExternalSolver solver;

  public ExternalSolverRunner(IParser.ParsedInput input, String tmpFilepath, IExternalSolver solver) throws IOException {
    super(input);
    tmpFile = new File(tmpFilepath);
    tmpFile.delete();
    writer = new BufferedWriter(new FileWriter(tmpFile, true));
    lineCount = new AtomicInteger(0);
    this.solver = solver;
  }

  @Override
  protected Optional<int[]> getModel(int[] assumptions) throws Exception {
    val file = prepareFile(assumptions);

    return solver.run(file);
  }

  private File prepareFile(int[] assumptions) throws IOException {
    writer.flush();

    val newFile = new File(tmpFile.getName() + ".tmp");
    newFile.delete();

    val header = "p cnf " + VarMap.size() + " " + (lineCount.get() + assumptions.length) + "\n";
    val newWriter = new BufferedWriter(new FileWriter(newFile, true));

    writeToAndIgnoreError(header, newWriter);
    newWriter.flush();

    val outWrite = new BufferedOutputStream(new FileOutputStream(newFile, true));
    val reader = new BufferedInputStream(new FileInputStream(tmpFile));
    ByteStreams.copy(reader, outWrite);
    outWrite.flush();


    Arrays.stream(assumptions)
        .mapToObj(Integer::toString)
        .forEach(a -> writeWithDelimiterTo(a, newWriter));

    newWriter.flush();
    return newFile;
  }

  @Override
  protected void addFormula(ClauseFormula formula) {
    formula.getClauses()
        .forEach(this::writeClause);
  }


  private void writeClause(VarClause clause) {
    lineCount.incrementAndGet();
    writeWithDelimiterTo(clause.toString(), writer);
  }


  private void writeWithDelimiterTo(String str, Writer wrt) {
    writeToAndIgnoreError(str + LINE_DELIMITER, wrt);
  }

  private void writeToAndIgnoreError(String str, Writer wrt) {
    try {
      wrt.write(str);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
