package smt.types;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Movement {
  final Position orig, dest;

  @Override
  public String toString() {
    return "(Movement " + orig + " " + dest + ')';
  }

  public String compareToMovStr() {
    return "(and (= orig " + orig + ") (= dest " + dest + "))";
  }
}