package sat.solver;

import fomatters.IParser;
import lombok.ToString;
import lombok.Value;
import lombok.val;
import table.Table;
import sat.variables.ClauseFormula;
import sat.variables.PositionVar;
import sat.variables.VarMap;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static sat.conditions.Conditions.*;

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
        val dir = table.directionFromCoords(lastPosition.j, currentPosition.j);
        moves.add(new Move(robotId, currentPosition.time, dir));
      }

      lastPositions[robotId] = currentPosition;
    }

    return moves;
  }

}
