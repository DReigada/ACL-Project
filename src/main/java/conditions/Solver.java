package conditions;

import fomatters.IParser;
import lombok.*;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IConstr;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import table.Table;
import variables.*;

import java.util.*;
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
        return Optional.of(getMoves(solver.model(), input, table));
      }
    }

    return Optional.empty();
  }

  private List<Move> getMoves(int[] sol, IParser.ParsedInput input, Table table) {
    val t = Arrays.stream(sol)
        .filter(a -> a > 0)
        .mapToObj(a -> VarMap.getById(Math.abs(a)))
        .filter(var -> var instanceof MovementVar)
        .map(var -> (MovementVar) var)
        .collect(Collectors.toList());

    List<Position> positions =
        input.getStartingPositions().stream()
            .map(a -> new Position(a.getRobot(), table.getCellId(a.getI(), a.getJ())))
            .collect(Collectors.toList());

    List<Move> moves = new LinkedList<>();

    Arrays.stream(sol)
        .filter(a -> a > 0)
        .mapToObj(a -> VarMap.getById(Math.abs(a)))
        .filter(var -> var instanceof PositionVar)
        .map(var -> (PositionVar) var)
        .filter(var -> {
          val test = new MovementVar(var.k, var.time - 1);
          return t.contains(test);
        })
        .sorted(Comparator.comparingInt(a -> a.time))
        .forEach(var -> {
          val oldPos = positions.stream().filter(a -> a.robot.toId() == var.k).findFirst().get();

          moves.add(new Move(var.k, var.time, dirFromCoords(oldPos.j, var.j, table)));
          oldPos.setJ(var.j);
        });

    return moves;
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
