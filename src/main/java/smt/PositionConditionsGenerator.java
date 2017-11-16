package smt;

import fomatters.IParser;
import lombok.val;
import smt.types.Position;
import table.Table;

import java.util.stream.Stream;

public class PositionConditionsGenerator {
  private static final String USED_TIME = "usedTime";


  public static Stream<String> generateMaxPosition(Table table) {
    return Stream.of(Integer.toString(table.getMaxPosition()));
  }

  public static Stream<String> generateInitialPositions(IParser.ParsedInput input, Table table) {
    return input.getStartingPositions().stream()
        .map(pos -> positionAssert(pos, "0", table));
  }

  public static String generateObjectivePosition(IParser.ParsedInput input, Table table, String time) {

    return positionAssert(input.getObjective(), time, table);
  }


  private static String positionAssert(IParser.Position pos, String time, Table table) {
    return "(assert(= position" + pos.getRobot() + time + " " + parserPositionToSmt(pos, table) + "))";
  }

  private static Position parserPositionToSmt(IParser.Position pos, Table table) {
    val posId = table.getCellId(pos.getI(), pos.getJ());
    return new Position(posId);
  }

}
