package fomatters;

import conditions.Solver;
import lombok.val;

import java.util.List;
import java.util.stream.Collectors;

public class OutputFormatter {

  public static String format(List<Solver.Move> moves) {
    return moves.stream()
        .map(move -> {
          val robot = IParser.Robot.fromId(move.getRobot()).toString().substring(0, 1).toUpperCase();
          val dir = move.getDir().toString().substring(0, 1).toLowerCase();
          return robot + " " + dir;
        })
        .collect(Collectors.joining("\n"));
  }
}
