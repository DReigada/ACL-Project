package csp;

import fomatters.InputParser;
import lombok.Value;
import lombok.val;
import table.EdgeWithDirection;
import table.Table;

import java.io.File;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helpers.StreamHelpers.range;
import static helpers.StreamHelpers.rangeClosed;

public class CSPApp {

//  @Value
//  private static class OrigDest {
//    int orig, dest;
//  }
//
//  @Value
//  private static class ArrayContent {
//    boolean isValid, isLast;
//  }

  public static void main(String[] args) throws Exception {
    val inputFile = new File("/Users/dreigada/IST/ALC/puzzles/puzzles-students/puzzle-54.rr");
    val input = new InputParser(inputFile).parse();

    val table = new Table(input);

    new Runner("").run(input);


//    String initialPos = input.getStartingPositions().stream()
//        .map(a -> {
//          val row = a.getRobot() + "PositionRow = " + (a.getI() + 1) + ";\n";
//          val col = a.getRobot() + "PositionCol = " + (a.getJ() + 1) + ";\n";
//          return row + col;
//        })
//        .collect(Collectors.joining(""));
//
//    val obj = input.getObjective();
//    val objectivePositionRow = "objectivePositionRow = " + (obj.getI() + 1) + ";\n";
//    val objectivePositionCol = "objectivePositionCol = " + (obj.getJ() + 1) + ";\n";
//    val objectiveRobot = "objectiveRobot = " + (obj.getRobot().toId() + 1) + ";\n";
//
//    System.out.println(initialPos);
//    System.out.println(objectivePositionRow);
//    System.out.println(objectivePositionCol);
//    System.out.println(objectiveRobot);
//
//    System.out.println(getPossibleMoves(table));
  }

//  private static String getPossibleMoves(Table table) {
//    Function<EdgeWithDirection, OrigDest> keyMapper = a -> new OrigDest(a.getOrig().getId(), a.getDest().getId());
//
//    Map<OrigDest, EdgeWithDirection> edges = table.getAllEdges()
//        .collect(Collectors.toMap(keyMapper, Function.identity()));
//
//
//    Stream<ArrayContent> t =
//        rangeClosed(1, table.getMaxPosition())
//            .flatMap(orig -> {
//              val line = getLine(orig, table).map(dest -> arrayContentFromEdge(orig, dest, edges));
//              val col = getCol(orig, table).map(dest -> arrayContentFromEdge(orig, dest, edges));
//              return Stream.concat(line, col);
//            });
//
//    return t.map(a -> a.isValid() + ", " + a.isLast())
//        .collect(Collectors.joining(", ", "[", "]"));
//  }
//
//  private static ArrayContent arrayContentFromEdge(int orig, int dest, Map<OrigDest, EdgeWithDirection> edges) {
//    val edge = edges.get(new OrigDest(orig, dest));
//    if (edge != null) {
//      return new ArrayContent(true, edge.isLast());
//    } else if (orig == dest) {
//      return new ArrayContent(true, true);
//    } else {
//      return new ArrayContent(false, false);
//    }
//  }
//
//  private static Stream<Integer> getLine(int i, Table table) {
//    int line = (i - 1) / table.getSize();
//    return rangeClosed(1, table.getSize()).map(ind -> table.getSize() * line + ind);
//  }
//
//  private static Stream<Integer> getCol(int i, Table table) {
//    int col = (i - 1) % table.getSize() + 1;
//    return range(0, table.getSize()).map(ind -> table.getSize() * ind + col);
//  }
}

