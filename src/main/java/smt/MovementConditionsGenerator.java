package smt;

import fomatters.InputParser;
import lombok.AllArgsConstructor;
import lombok.val;
import table.ConnectedCell;
import table.EdgeWithDirection;
import table.Table;

import java.io.File;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovementConditionsGenerator {
  public static Stream<String> noRobotsBetweenOrigAndDest(Table table) {
    Stream<EdgeWithDirection> edges =
        table
            .getAllEdges();

    return edges.map(edge -> {
      val mov = edgeToMove(edge);
      val inbetweens = edge.getStream().map(MovementConditionsGenerator::cellToPosition);
      val nextPos = edge.getNextCellOpt().map(MovementConditionsGenerator::cellToPosition);
      return transformToCondition(mov, inbetweens, nextPos);
    });
  }

  private static String transformToCondition(Movement mov, Stream<Position> inbetweens, Optional<Position> nextPos) {
    val mv = mov.compareToMovStr();
    val inb = inbetweensString(inbetweens).orElse("");
    val next = nextPos.map(MovementConditionsGenerator::nextPosString).orElse("");

    return "(and " + mv + " " + inb + " " + next + ")";
  }

  private static String nextPosString(Position nextPos) {
    return nextPos.isPositionFilledStr();
  }

  private static Optional<String> inbetweensString(Stream<Position> inbetweens) {
    val t = inbetweens
        .map(Position::isPositionFilledStr)
        .collect(Collectors.toList());

    if (t.isEmpty()) {
      return Optional.empty();
    } else {
      val res =
          t.stream()
              .collect(Collectors.joining(" ", "(not (or ", "))"));
      return Optional.of(res);
    }
  }

  private static Movement edgeToMove(EdgeWithDirection edge) {
    return new Movement(cellToPosition(edge.getOrig()), cellToPosition(edge.getDest()));
  }

  private static Position cellToPosition(ConnectedCell cell) {
    return new Position(cell.getId());
  }

  @AllArgsConstructor
  static class Movement {
    final Position orig, dest;

    @Override
    public String toString() {
      return "(Movement " + orig + " " + dest + ')';
    }

    public String compareToMovStr() {
      return "(= mov " + toString() + ")";
    }
  }


  @AllArgsConstructor
  static class Position {
    final int j;

    @Override
    public String toString() {
      return "(Position " + j + ')';
    }

    public String isPositionFilledStr() {
      return "(isPositionFilled " + toString() + " time)";
    }
  }

  public static void main(String[] args) throws Exception {

    val file = new File("/Users/dreigada/IST/ALC/puzzles/small_puzzle/my.rr");
    val input = new InputParser(file).parse();
    val table = new Table(input);


    val test = noRobotsBetweenOrigAndDest(table).collect(Collectors.joining("\n"));

    System.out.println(test);
  }

}

