package csp;

import fomatters.IParser;
import lombok.Value;
import lombok.val;
import table.EdgeWithDirection;
import table.Table;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helpers.StreamHelpers.range;
import static helpers.StreamHelpers.rangeClosed;

public class DZNGenerator {

  @Value
  private static class OrigDest {
    int orig, dest;
  }

  @Value
  private static class ArrayContent {
    boolean isValid, isLast;
  }

  public Stream<String> getDZN(IParser.ParsedInput input, Table table) {
    val boardInfo = getBoardInfo(table);
    val initialPos = getInitialPositionsStr(input);
    val objectives = getObjectives(input);
    val possibleMoves = getPossibleMoves(table);

    return Stream.concat(boardInfo, Stream.concat(initialPos, Stream.concat(objectives, possibleMoves)))
        .map(a -> a + ";\n");
  }

  private static Stream<String> getBoardInfo(Table table) {
    return Stream.of("tableSize = " + table.getSize());
  }


  private static Stream<String> getInitialPositionsStr(IParser.ParsedInput input) {
    return input.getStartingPositions().stream()
        .flatMap(a -> {
          val row = a.getRobot() + "PositionRow = " + (a.getI() + 1);
          val col = a.getRobot() + "PositionCol = " + (a.getJ() + 1);
          return Stream.of(row, col);
        });
  }


  private static Stream<String> getObjectives(IParser.ParsedInput input) {
    val obj = input.getObjective();
    val objectivePositionRow = "objectivePositionRow = " + (obj.getI() + 1);
    val objectivePositionCol = "objectivePositionCol = " + (obj.getJ() + 1);
    val objectiveRobot = "objectiveRobot = " + (obj.getRobot().toId() + 1);

    return Stream.of(objectivePositionRow, objectivePositionCol, objectiveRobot);
  }

  private static Stream<String> getPossibleMoves(Table table) {
    return Stream.of("possibleMoves = array5d(1..tableSize, 1..tableSize, 0..1, 1..tableSize, 0..1, " +
        getPossibleMovesArray(table) + ")");
  }

  private static String getPossibleMovesArray(Table table) {
    Function<EdgeWithDirection, OrigDest> keyMapper = a -> new OrigDest(a.getOrig().getId(), a.getDest().getId());

    Map<OrigDest, EdgeWithDirection> edges = table.getAllEdges()
        .collect(Collectors.toMap(keyMapper, Function.identity()));


    Stream<ArrayContent> t =
        rangeClosed(1, table.getMaxPosition())
            .flatMap(orig -> {
              val line = getLine(orig, table).map(dest -> arrayContentFromEdge(orig, dest, edges));
              val col = getCol(orig, table).map(dest -> arrayContentFromEdge(orig, dest, edges));
              return Stream.concat(line, col);
            });

    return t.map(a -> a.isValid() + ", " + a.isLast())
        .collect(Collectors.joining(", ", "[", "]"));
  }

  private static ArrayContent arrayContentFromEdge(int orig, int dest, Map<OrigDest, EdgeWithDirection> edges) {
    val edge = edges.get(new OrigDest(orig, dest));
    if (edge != null) {
      return new ArrayContent(true, edge.isLast());
    } else if (orig == dest) {
      return new ArrayContent(true, true);
    } else {
      return new ArrayContent(false, false);
    }
  }

  private static Stream<Integer> getLine(int i, Table table) {
    int line = (i - 1) / table.getSize();
    return rangeClosed(1, table.getSize()).map(ind -> table.getSize() * line + ind);
  }

  private static Stream<Integer> getCol(int i, Table table) {
    int col = (i - 1) % table.getSize() + 1;
    return range(0, table.getSize()).map(ind -> table.getSize() * ind + col);
  }
}
