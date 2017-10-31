package conditions;

import fomatters.IParser;
import lombok.Value;
import lombok.val;
import table.ConnectedCell;
import table.Table;
import variables.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static helpers.StreamHelpers.range;
import static helpers.StreamHelpers.rangeClosed;

public class Conditions {
  public static int NUM_ROBOTS = 4;

  public static ClauseFormula initialPositionFormulae(Table table, IParser.ParsedInput input) {
    Stream<VarClause> f = input.getStartingPositions().stream()
        .map(pos -> {
          val posId = table.getCellId(pos.getI(), pos.getJ());
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
                  val var1 = new PositionVar(j, robot, time).negated();
                  val var2 = new PositionVar(l, robot, time).negated();
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
              val var1 = new MovementVar(k, time).negated();
              val var2 = new MovementVar(h, time).negated();
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
                                  .negated()));

          return vars.map(var ->
              new PossibleMoveVar(edge.getOrig().getId(), edge.getDest().getId(), time)
                  .implies(var));

        });

    return new ClauseFormula(clauses);
  }

  public static ClauseFormula robotEitherMovedOrWasAlreadyInPlace(Table table, int time) {
    return table.getAllConnectedCells()
        .flatMap(cell ->
            // TODO this could be improved if only done for one robot and then copy for all the others
            robotRange().map(robot ->
                combinationsForCellAndRobot(cell, robot, time)
            ))
        .reduce(ClauseFormula.empty(), ClauseFormula::concat);
  }

  private static ClauseFormula combinationsForCellAndRobot(ConnectedCell position, int robot, int time) {
    val destinationId = position.getId();
    Stream<Acc1> s = position.listAllConnected().map(edge -> {
      // the edges are reversed! the origin is the destination
      val pos = new PositionVar(edge.getDest().getId(), robot, time);
      val posMov = new PossibleMoveVar(edge.getDest().getId(), destinationId, time);
      val mov = new MovementVar(robot, time);

      AuxVar auxVar = new AuxVar(pos, posMov, mov);
      return new Acc1(auxVar, auxVar.getImplications());
    });

    Acc2 acc = s.reduce(new Acc2(Stream.empty(), Stream.empty()), Acc2::addTest, Acc2::join);

    val posT = new PositionVar(position.getId(), robot, time);
    val posTPlus1 = new PositionVar(position.getId(), robot, time + 1).negated(); // this one implies the rest

    return acc.addAux(posT, posTPlus1).asClauseFormula();
  }


  // Helper Classes for method conbinationsForCellAndRobot
  @Value
  private static class Acc1 {
    AuxVar auxVar;
    Stream<VarClause> implications;
  }

  @Value
  private static class Acc2 {
    Stream<Variable> auxVars;
    Stream<VarClause> implications;

    public Acc2 addAux(Variable... auxs) {
      val newAuxs = Stream.concat(auxVars, Arrays.stream(auxs));
      return new Acc2(newAuxs, implications);
    }

    public Acc2 addTest(Acc1 elem) {
      val newAuxs = Stream.concat(auxVars, Stream.of(elem.getAuxVar()));
      val newImpl = Stream.concat(implications, elem.implications);
      return new Acc2(newAuxs, newImpl);
    }

    public Acc2 join(Acc2 other) {
      val newAuxs = Stream.concat(auxVars, other.getAuxVars());
      val newImpl = Stream.concat(implications, other.getImplications());
      return new Acc2(newAuxs, newImpl);
    }

    public ClauseFormula asClauseFormula() {
      val form = new ClauseFormula(implications);
      form.addClause(Stream.of(new VarClause(auxVars)));
      return form;
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
