package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PositionVar extends Variable {

  private int j, k;
  private int time;

  public PositionVar(int j, int k, int t) {
    this.time = t;
    this.j = j;
    this.k = k;
  }

  @Override
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "PositionVar(" + "j=" + j + ", k=" + k + ", t=" + time + ')';
  }

}
