package asp;

import fomatters.IParser;
import lombok.Value;
import lombok.val;
import table.Table;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OutputParser {
  private final BufferedReader reader;

  private final static String MOVE_PREFIX = "move";
  private final static Pattern LINE_PATTERN = Pattern.compile("move\\((.*),(.*),(.*),(.*)\\)");

  @Value
  public static class Move {
    Table.Direction dir;
    IParser.Robot robot;
    int time;
  }

  public OutputParser(BufferedReader reader) {
    this.reader = reader;
  }

  public Optional<List<Move>> parse() {
    val moves = reader
        .lines()
        .filter(line -> line.startsWith(MOVE_PREFIX))
        .flatMap(line -> Arrays.stream(line.split(" ")))
        .map(this::parseMove)
        .collect(Collectors.toList());


    // UNSAT
    if (moves.isEmpty()) {
      return Optional.empty();
    }

    moves.sort(Comparator.comparingInt(a -> a.time));

    return Optional.of(moves);
  }

  private Move parseMove(String line) {
    val matcher = LINE_PATTERN.matcher(line);
    matcher.find();

    val robotStr = matcher.group(1);
    val colDir = Integer.parseInt(matcher.group(2));
    val rowDir = Integer.parseInt(matcher.group(3));
    val time = Integer.parseInt(matcher.group(4));

    val robot = IParser.Robot.fromString(robotStr.trim().substring(0, 1));
    val direction = directionFromDirs(colDir, rowDir);

    return new Move(direction, robot, time);
  }


  private Table.Direction directionFromDirs(int colDir, int rowDir) {
    if (colDir == 1) return Table.Direction.Right;
    else if (colDir == -1) return Table.Direction.Left;
    else if (rowDir == 1) return Table.Direction.Down;
    else if (rowDir == -1) return Table.Direction.Up;
    else throw new RuntimeException("Invalid direction");
  }

}
