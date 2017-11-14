package smt;

import fomatters.IParser;
import lombok.val;
import smt.types.Position;
import table.Table;

import java.util.stream.Stream;

public class PositionConditionsGenerator {
  private static final String USED_TIME = "usedTime";


  public static Stream<String> generate(IParser.ParsedInput input, Table table) {
    Stream<String> initialPosConditions = input.getStartingPositions().stream()
        .map(pos -> positionAssert(pos, "0", table));

    String objectivePosCondition = positionAssert(input.getObjective(), USED_TIME, table);

    return Stream.concat(initialPosConditions, Stream.of(objectivePosCondition));
  }


  private static String positionAssert(IParser.Position pos, String time, Table table) {
    return "(assert(= (position " + pos.getRobot() + " " + time + ") " + parserPositionToSmt(pos, table) + "))";
  }

  private static Position parserPositionToSmt(IParser.Position pos, Table table) {
    val posId = table.getCellId(pos.getI(), pos.getJ());
    return new Position(posId);
  }

}
