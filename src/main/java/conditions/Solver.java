package conditions;

import fomatters.IParser;
import lombok.ToString;
import lombok.Value;
import lombok.val;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import table.Table;
import variables.ClauseFormula;
import variables.PositionVar;
import variables.VarMap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static conditions.Conditions.*;

public class Solver {
  private final IParser.ParsedInput input;
  private final Table table;
  private final ISolver solver;

  public Solver(IParser.ParsedInput input) {
    this.input = input;
    table = new Table(input);
    solver = SolverFactory.newDefault();
  }

  public Optional<List<Move>> solve(int maxSteps) throws TimeoutException, ContradictionException {
    return doSolve(table, maxSteps).map(this::toMoves);
  }

  private Optional<int[]> doSolve(Table table, int maxSteps) throws TimeoutException {

    addFormula(initialPositionFormula(table, input));

    for (int stepN = 0; stepN <= maxSteps; stepN++) {
      // these go from 0 to maxSteps
      addFormula(robotMustHavePosition(table, stepN));
      addFormula(robotCanNotHaveTwoPositions(table, stepN));
      val sat = solver.isSatisfiable(objectiveFormula(table, input.getObjective(), stepN).asVecInt());

      if (sat) {
        return Optional.of(solver.model());
      } else if (stepN == maxSteps) {
        return Optional.empty();
      }

      // these only go from 0 to maxSteps - 1
      addFormula(robotMustHavePosition(table, stepN));
      addFormula(robotCanNotHaveTwoPositions(table, stepN));
      addFormula(robotEitherMovedOrWasAlreadyInPlace(table, stepN));
      addFormula(noRobotsBetweenOrigAndDest(table, stepN));
      addFormula(stopVertex(table, stepN));
      addFormula(onlyOneRobotCanMoveEachTimeStep(stepN));
    }

    return Optional.empty();
  }

  private List<Move> toMoves(int[] sol) {
    val positions = Arrays.stream(sol)
        .filter(a -> a > 0)
        .mapToObj(a -> VarMap.getById(Math.abs(a)))
        .filter(var -> var instanceof PositionVar)
        .map(var -> (PositionVar) var)
        .collect(Collectors.toList());

    return listMovesFromPositions(positions);
  }

  private List<Move> listMovesFromPositions(List<PositionVar> positions) {
    PositionVar[] lastPositions = new PositionVar[4];
    List<Move> moves = new LinkedList<>();

    for (PositionVar currentPosition : positions) {
      val robotId = currentPosition.k;

      PositionVar lastPosition = lastPositions[robotId];

      if (lastPosition != null && lastPosition.j != currentPosition.j) { //robot moved
        val dir = directionFromCoords(lastPosition.j, currentPosition.j, table);
        moves.add(new Move(robotId, currentPosition.time, dir));
      }

      lastPositions[robotId] = currentPosition;
    }

    return moves;
  }

  private void addFormula(ClauseFormula formula) {
    formula.getClauses().forEach(clause -> {
      try {
        solver.addClause(clause.asVecInt());
      } catch (ContradictionException e) {
        e.printStackTrace();
        System.exit(1);
      }
    });
  }

  private static Table.Direction directionFromCoords(int from, int to, Table table) {
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

  @Value
  @ToString
  public static class Move {
    int robot, time;
    Table.Direction dir;
  }

}
