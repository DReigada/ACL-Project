package fomatters;

import lombok.Value;
import table.Table;

import java.util.List;

public abstract class IParser {

  abstract public ParsedResult parse() throws Exception;

  @Value
  public class ParsedResult {
    int size;
    List<Position> startingPositions;
    Position objective;
    List<Wall> walls;
  }

  @Value
  public class Position {
    Robot robot;
    int i, j;
  }

  @Value
  public class Wall {
    int i, j;
    Table.Direction direction;
  }


  public enum Robot {
    Red, Yellow, Blue, Green;

    public static Robot fromString(String str) {
      switch (str.toLowerCase()) {
        case "r":
          return Red;
        case "y":
          return Yellow;
        case "b":
          return Blue;
        case "g":
          return Green;
        default:
          throw new RuntimeException("Invalid Robot: " + str);
      }
    }
  }

  protected Table.Direction directionFromString(String str) {
    switch (str.toLowerCase()) {
      case "u":
        return Table.Direction.Up;
      case "d":
        return Table.Direction.Down;
      case "l":
        return Table.Direction.Left;
      case "r":
        return Table.Direction.Right;
      default:
        throw new RuntimeException("Invalid Direction: " + str);
    }
  }
}
