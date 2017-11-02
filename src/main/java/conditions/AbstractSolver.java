package conditions;

import fomatters.IParser;
import lombok.ToString;
import lombok.Value;
import lombok.val;
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

public abstract class AbstractSolver {
  protected final IParser.ParsedInput input;
  protected final Table table;

  @Value
  @ToString
  public static class Move {
    int robot, time;
    Table.Direction dir;
  }

  public AbstractSolver(IParser.ParsedInput input) {
    this.input = input;
    table = new Table(input);
  }

  protected abstract Optional<int[]> getModel(int[] assumption) throws Exception;

  protected abstract void addFormula(ClauseFormula formula);

  public Optional<List<Move>> solve(int maxSteps) throws Exception {
    return doSolve(table, maxSteps).map(this::toMoves);
  }

  private Optional<int[]> doSolve(Table table, int maxSteps) throws Exception {

    addFormula(initialPositionFormula(table, input));

    for (int stepN = 0; stepN <= maxSteps; stepN++) {
      // these go from 0 to maxSteps
      addFormula(robotMustHavePosition(table, stepN));
      addFormula(robotCanNotHaveTwoPositions(table, stepN));

      val model = getModel(new int[]{objectiveFormula(table, input.getObjective(), stepN).id()});
      if (model.isPresent()) {
        return model;
      } else if (stepN == maxSteps) {
        return Optional.empty();
      }

      // these only go from 0 to maxSteps - 1
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

}
