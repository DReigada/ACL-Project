package conditions;

import fomatters.IParser;
import lombok.val;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import table.Table;
import variables.ClauseFormula;
import variables.VarClause;

import static conditions.Conditions.*;

public class Solver {
  private ISolver solver;
  private IParser.ParsedInput input;


  public Solver(IParser.ParsedInput input) {
    this.input = input;
    solver = SolverFactory.newDefault();
  }

  public int[] solve() throws TimeoutException, ContradictionException {
    Table table = new Table(input);

    // TODO improve this to only add the clauses
    for (int i = 1; i < 10; i++) {
      doSolve(table, i);

      if (solver.isSatisfiable()) {
        System.out.println("Found solution. Steps: " + i);
        return solver.model();
      }
    }

    return null;
  }

  private void doSolve(Table table, int steps) throws ContradictionException {
    solver.reset();

    for (int time = 0; time < steps; time++) {
      val c1 = robotMustHavePosition(table, time);
      addFormula(c1);

      val c2 = robotCanNotHaveTwoPositions(table, time);
      addFormula(c2);

      val c6 = robotEitherMovedOrWasAlreadyInPlace(table, time);
      addFormula(c6);

      val c5 = noRobotsBetweenOrigAndDest(table, time);
      addFormula(c5);

      val c4 = stopVertex(table, time);
      addFormula(c4);

      val c3 = onlyOneRobotCanMoveEachTimeStep(time);
      addFormula(c3);

    }
    val c11 = robotMustHavePosition(table, steps);
    addFormula(c11);
    val c21 = robotCanNotHaveTwoPositions(table, steps);
    addFormula(c21);

    val init = initialPositionFormulae(table, input);
    addFormula(init);

    val obj = objectiveFormula(table, input.getObjective(), steps);
    addClause(obj);
  }


  private IConstr addClause(VarClause clause) throws ContradictionException {
    return solver.addClause(clause.asVecInt());
  }


  private void addFormula(ClauseFormula formula) {
    formula.getClauses().forEach(clause -> {
      try {
        addClause(clause);
      } catch (ContradictionException e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
  }

}
