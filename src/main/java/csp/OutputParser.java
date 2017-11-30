package csp;

import fomatters.IParser;
import lombok.Value;
import lombok.val;
import sat.solver.AbstractSolver;
import table.Table;

import java.io.BufferedReader;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutputParser {
  private final Table table;
  private final BufferedReader reader;

  private final static String POSITIONS_PREFIX = "positions ";
  private final static Pattern LINE_PATTERN = Pattern.compile("(.*): (.*)");

  @Value
  private static class Position {
    int i, j;
  }

  @Value
  private static class RobotPositions {
    IParser.Robot robot;
    List<Position> positions;
  }

  public OutputParser(Table table, BufferedReader reader) {
    this.table = table;
    this.reader = reader;
  }

  public Optional<Stream<AbstractSolver.Move>> parse() {
    val positions = reader
        .lines()
        .filter(line -> line.startsWith(POSITIONS_PREFIX))
        .map(line -> line.replaceFirst(POSITIONS_PREFIX, ""))
        .map(this::parseLine)
        .collect(Collectors.toList());

    val moves = new LinkedList<AbstractSolver.Move>();

    // UNSAT
    if (positions.isEmpty()) {
      return Optional.empty();
    }

    for (val robotPos : positions) {
      int time = 0;
      Position oldPosition = robotPos.positions.get(0);

      for (val position : robotPos.positions) {
        if (!position.equals(oldPosition)) {
          val from = table.getCellId(oldPosition.i - 1, oldPosition.j - 1);
          val to = table.getCellId(position.i - 1, position.j - 1);
          val direction = table.directionFromCoords(from, to);

          oldPosition = position;
          moves.add(new AbstractSolver.Move(robotPos.robot.toId(), time, direction));
        }
        time += 1;
      }
    }

    moves.sort(Comparator.comparingInt(a -> a.getTime()));

    return Optional.of(moves.stream());
  }

  private RobotPositions parseLine(String line) {
    val matcher = LINE_PATTERN.matcher(line);
    matcher.find();

    val robotStr = matcher.group(1);
    val positionsStr = matcher.group(2);

    val robot = IParser.Robot.fromString(robotStr.trim());

    val positions = Arrays.stream(positionsStr.split("->"))
        .map(a -> a.split(","))
        .map(coords -> new Position(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])))
        .collect(Collectors.toList());

    return new RobotPositions(robot, positions);
  }

}
