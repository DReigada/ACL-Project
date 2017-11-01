package conditions;

import fomatters.IParser;
import lombok.val;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import variables.ClauseFormula;

import java.util.Optional;

public class Sat4JSolver extends AbstractSolver {
  private final ISolver solver;

  public Sat4JSolver(IParser.ParsedInput input) {
    super(input);
    solver = SolverFactory.newDefault();
  }

  @Override
  protected Optional<int[]> getModel(int[] assumption) throws TimeoutException {
    val sat = solver.isSatisfiable(new VecInt(assumption));
    if (sat) {
      return Optional.of(solver.model());
    } else {
      return Optional.empty();
    }
  }

  @Override
  protected void addFormula(ClauseFormula formula) {
    formula.getClauses().forEach(clause -> {
      try {
        solver.addClause(new VecInt(clause.getIds()));
      } catch (ContradictionException e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
  }

}
