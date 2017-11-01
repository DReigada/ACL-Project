package conditions;

import fomatters.IParser;
import lombok.*;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import table.Table;
import variables.ClauseFormula;
import variables.PositionVar;
import variables.VarClause;
import variables.VarMap;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static conditions.Conditions.*;

public class Solver {
  private ISolver solver;
  private IParser.ParsedInput input;


  public Solver(IParser.ParsedInput input) {
    this.input = input;
    solver = SolverFactory.newDefault();
  }

  public Optional<List<Move>> solve(int maxSteps) throws TimeoutException, ContradictionException {
    Table table = new Table(input);

    // TODO improve this to only add the clauses
    for (int i = 1; i < maxSteps; i++) {
      doSolve(table, i);

      if (solver.isSatisfiable()) {
        return Optional.of(getMoves(solver.model(), table));
      }
    }

    return Optional.empty();
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


  private List<Move> getMoves(int[] sol, Table table) {
    val positions = Arrays.stream(sol)
        .filter(a -> a > 0)
        .mapToObj(a -> VarMap.getById(Math.abs(a)))
        .filter(var -> var instanceof PositionVar)
        .map(var -> (PositionVar) var)
        .collect(Collectors.toList());

    return listMovesFromPositions(positions, table);
  }

  private List<Move> listMovesFromPositions(List<PositionVar> positions, Table table) {
    PositionVar[] lastPositions = new PositionVar[4];
    List<Move> moves = new LinkedList<>();

    for (PositionVar currentPosition : positions) {
      val robotId = currentPosition.k;

      PositionVar lastPosition = lastPositions[robotId];

      if (lastPosition != null && lastPosition.j != currentPosition.j) { //robot moved
        val dir = dirFromCoords(lastPosition.j, currentPosition.j, table);
        moves.add(new Move(robotId, currentPosition.time, dir));
      }

      lastPositions[robotId] = currentPosition;
    }

    return moves;
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

  private static Table.Direction dirFromCoords(int from, int to, Table table) {
    val fromCoords = table.getCoordsFromId(from);
    val toCoords = table.getCoordsFromId(to);

    if (toCoords[0] - fromCoords[0] > 0) {
      return Table.Direction.Down;
    } else if (toCoords[0] - fromCoords[0] < 0) {
      return Table.Direction.Up;
    } else if (toCoords[1] - fromCoords[1] > 0) {
      return Table.Direction.Right;
    } else if (toCoords[1] - fromCoords[1] < 0) {
      return Table.Direction.Left;
    } else {
      throw new RuntimeException("This should never happen");
    }
  }


  @Setter
  @AllArgsConstructor
  static class Position {
    IParser.Robot robot;
    int j;
  }

  @Value
  @ToString
  public static class Move {
    int robot, time;
    Table.Direction dir;
  }

}
