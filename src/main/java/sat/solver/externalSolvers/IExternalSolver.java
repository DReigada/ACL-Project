package sat.solver.externalSolvers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface IExternalSolver {

  Optional<int[]> run(File file) throws IOException;
}
