package asp;

import fomatters.InputParser;
import lombok.val;

import java.util.stream.Stream;

public class FactsGenerator {

  public static Stream<String> generate(InputParser.ParsedInput input) {
    val pos = generatePositions(input);
    val target = generateTarget(input);
    val barriers = generateBarriers(input);

    return Stream.concat(pos, Stream.concat(Stream.of(target), barriers));
  }

  private static Stream<String> generatePositions(InputParser.ParsedInput input) {
    return input.getStartingPositions()
        .stream()
        .map(s -> "position(" + s.getRobot().name().toLowerCase() + ", " + s.getCol() + ", " + s.getRow() + ").");
  }

  private static String generateTarget(InputParser.ParsedInput input) {
    val obj = input.getObjective();
    return "target(" + obj.getRobot().name().toLowerCase() + ", " + obj.getCol() + ", " + obj.getRow() + ").";
  }

  private static Stream<String> generateBarriers(InputParser.ParsedInput input) {
    return input.getWalls()
        .stream()
        .map(w -> {
          switch (w.getDirection()) {
            case Up:
              return barriersFactString(w.getRow(), w.getCol() - 1, false);
            case Down:
              return barriersFactString(w.getRow(), w.getCol(), false);
            case Left:
              return barriersFactString(w.getRow() - 1, w.getCol(), true);
            case Right:
              return barriersFactString(w.getRow(), w.getCol(), true);
            default:
              throw new RuntimeException("This should not happen");
          }

        });
  }

  private static String barriersFactString(int i, int j, boolean horizontal) {
    return "barrier(" + j + ", " + i + ", " + (horizontal ? 1 : 0) + ", " + (horizontal ? 0 : 1) + ").";
  }
}
