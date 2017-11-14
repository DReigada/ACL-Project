package sat.variables;

import lombok.EqualsAndHashCode;
import lombok.val;

@EqualsAndHashCode(callSuper = true)
public class PositionVar extends Variable {

  public final int j, k;
  public final int time;

  public PositionVar(int j, int k, int t) {
    this.time = t;
    this.j = j;
    this.k = k;
  }

  @Override
  public Variable copy() {
    val mov = new PositionVar(j, k, time);
    if (isNegated()) {
      return mov.negated();
    } else {
      return mov;
    }
  }

  @Override
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "PositionVar(" + "j=" + j + ", k=" + k + ", t=" + time + ')';
  }

}
