package conditions;

import fomatters.IParser;
import lombok.val;
import table.ConnectedCell;
import table.Table;
import variables.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static helpers.StreamHelpers.range;
import static helpers.StreamHelpers.rangeClosed;

public class Conditions {
  public static int NUM_ROBOTS = 4;

  public static ClauseFormula initialPositionFormulae(Table table, IParser.ParsedInput input) {
    Stream<VarClause> f = input.getStartingPositions().stream()
        .map(pos -> {
          val posId = table.getCellId(pos.getI(), pos.getI());
          val robot = robotToId(pos.getRobot());
          return new VarClause(new PositionVar(posId, robot, 0));
        });

    return new ClauseFormula(f);
  }

  public static VarClause objectiveFormula(Table table, IParser.Position objective, int time) {
    val posId = table.getCellId(objective.getI(), objective.getJ());
    val robot = robotToId(objective.getRobot());

    return new VarClause(new PositionVar(posId, robot, time));
  }

  public static ClauseFormula robotMustHavePosition(Table table, int time) {
    int maxPos = table.getMaxPosition();

    Stream<VarClause> clauses =
        robotRange().map(robot -> {
          val positions = IntStream.rangeClosed(1, maxPos)
              .mapToObj(j -> new PositionVar(j, robot, time));
          return new VarClause(positions);
        });

    return new ClauseFormula(clauses);
  }

  public static ClauseFormula robotCanNotHaveTwoPositions(Table table, int time) {
    int maxPos = table.getMaxPosition();

    Stream<VarClause> clauses =
        robotRange().flatMap(robot ->
            rangeClosed(1, maxPos).flatMap(j ->
                rangeClosed(j + 1, maxPos).map(l -> {
                  val var1 = new PositionVar(j, robot, time).negate();
                  val var2 = new PositionVar(l, robot, time).negate();
                  return new VarClause(var1, var2);
                })
            )
        );

    return new ClauseFormula(clauses);
  }

  public static ClauseFormula onlyOneRobotCanMoveEachTimeStep(int time) {
    Stream<VarClause> clauses = robotRange()
        .flatMap(k ->
            range(k + 1, NUM_ROBOTS).map(h -> {
              val var1 = new MovementVar(k, time).negate();
              val var2 = new MovementVar(h, time).negate();
              return new VarClause(var1, var2);
            })
        );

    return new ClauseFormula(clauses);

  }

  public static ClauseFormula stopVertex(Table table, int time) {
    Stream<VarClause> clauses = table.getAllEdges()
        .filter(edge -> !edge.isLast())
        .map(edge -> {
          val t = robotRange().map(robot -> new PositionVar(edge.getNextId(), robot, time));
          val clause = new VarClause(t);
          return new PossibleMoveVar(edge.getOrig().getId(), edge.getDest().getId(), time).implies(clause);
        });

    return new ClauseFormula(clauses);
  }

  public static ClauseFormula noRobotsBetweenOrigAndDest(Table table, int time) {
    Stream<VarClause> clauses = table.getAllEdges()
        .flatMap(edge -> {
          Stream<Variable> vars =
              edge
                  .getStream()
                  .flatMap(cell ->
                      robotRange()
                          .map(robot ->
                              new PositionVar(cell.getId(), robot, time)
                                  .negate()));

          return vars.map(var ->
              new PossibleMoveVar(edge.getOrig().getId(), edge.getDest().getId(), time)
                  .implies(var));

        });

    return new ClauseFormula(clauses);
  }

  public static ClauseFormula test(Table table, int time) {
    return table.getAllConnectedCells()
        .flatMap(cell ->
            robotRange().map(robot ->
                bla(cell, robot, time)
            ))
        .reduce(ClauseFormula.empty(), ClauseFormula::concat);
  }


  private static ClauseFormula bla(ConnectedCell position, int robot, int time) {
    val positionVarT = new PositionVar(position.getId(), robot, time);
    val positionVarTPlusOne = new PositionVar(position.getId(), robot, time + 1);

    List<List<Variable>> s = position.listAllConnected().map(edge ->
        Arrays.asList(
            new PositionVar(edge.getOrig().getId(), robot, time),
            new PossibleMoveVar(edge.getOrig().getId(), edge.getOrig().getId(), time),
            new MovementVar(robot, time))
    ).collect(Collectors.toList());

    s.add(Collections.singletonList(positionVarT));
    s.add(Collections.singletonList(positionVarTPlusOne.negate())); // this one implies the rest

    Stream<VarClause> clauses =
        combinations(s, new LinkedList<>()).stream()
            .map(l -> new VarClause(l.stream()));

    return new ClauseFormula(clauses);
  }

  private static List<List<Variable>> combinations(List<List<Variable>> list, List<List<Variable>> acc) {
    if (list.size() == 0) {
      return acc;
    } else if (acc.size() == 0) {
      val newAcc = head(list).stream()
          .map(a -> {
            List<Variable> tempList = new LinkedList<>();
            tempList.add(a);
            return tempList;
          })
          .collect(Collectors.toList());
      return combinations(tail(list), newAcc);
    } else {
      val t = head(list).stream()
          .flatMap(var ->
              acc.stream()
                  .map(clause -> {
                        List<Variable> newClause = new LinkedList<>(clause);
                        newClause.add(var);
                        return newClause;
                      }
                  )).collect(Collectors.toList());
      return combinations(tail(list), t);
    }
  }

  private static <A> A head(List<A> list) {
    return list.get(0);
  }

  private static <A> List<A> tail(List<A> list) {
    return list.subList(1, list.size());
  }

  private static Stream<Integer> robotRange() {
    return range(0, NUM_ROBOTS);
  }

  private static int robotToId(IParser.Robot robot) {
    switch (robot) {
      case Red:
        return 0;
      case Yellow:
        return 1;
      case Blue:
        return 2;
      case Green:
        return 3;
      default:
        throw new RuntimeException("Invalid Robot This should never happen");
    }
  }
}
