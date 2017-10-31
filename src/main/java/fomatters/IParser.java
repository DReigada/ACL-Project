package fomatters;

import lombok.Value;
import table.Table;

import java.util.List;

public abstract class IParser {

  abstract public ParsedInput parse() throws Exception;

  @Value
  public class ParsedInput {
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

    public int toId() {
      switch (this) {
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

    public static IParser.Robot fromId(int id) {
      switch (id) {
        case 0:
          return IParser.Robot.Red;
        case 1:
          return IParser.Robot.Yellow;
        case 2:
          return IParser.Robot.Blue;
        case 3:
          return IParser.Robot.Green;
        default:
          throw new RuntimeException("Invalid Robot This should never happen");
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
