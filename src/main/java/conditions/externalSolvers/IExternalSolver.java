package conditions.externalSolvers;

import java.io.File;
import java.util.Optional;

public interface IExternalSolver {

  public Optional<int[]> run(File file);
}
