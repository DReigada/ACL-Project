package asp;

import lombok.val;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class OutputFormatter {

  public static String format(List<OutputParser.Move> moves) {
    String formattedMoves = moves.stream()
        .map(move -> move.getTime() + " "
            + move.getRobot().name().substring(0, 1).toUpperCase() + " "
            + move.getDir().toString().substring(0, 1).toLowerCase())
        .collect(Collectors.joining("\n"));

    val usedMoves = moves.stream().max(Comparator.comparingInt(OutputParser.Move::getTime))
        .map(OutputParser.Move::getTime)
        .orElse(0);

    return usedMoves + "\n" + formattedMoves;
  }
}
