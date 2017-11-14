package smt;

import lombok.val;
import smt.types.Movement;
import smt.types.Position;
import table.ConnectedCell;
import table.EdgeWithDirection;
import table.Table;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovementConditionsGenerator {
  public static Stream<String> generateMovementConditions(Table table) {
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

}

