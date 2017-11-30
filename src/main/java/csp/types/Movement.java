package csp.types;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Movement {
  final Position orig, dest;

  @Override
  public String toString() {
    return "(Movement " + orig + " " + dest + ')';
  }

  public String compareToMovStr() {
    return "( movements[step, 0] = " + orig + " /\\ movements[step, 1] = " + dest + " )";
  }
}