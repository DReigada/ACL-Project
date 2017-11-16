package smt.types;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Position {
  final int j;

  @Override
  public String toString() {
    return j + "";
  }

  public String isPositionFilledStr() {
    return "(isPositionFilled " + toString() + " positionRed positionYellow positionGreen positionBlue)";
  }
}