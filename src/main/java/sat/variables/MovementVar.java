package sat.variables;

import lombok.EqualsAndHashCode;
import lombok.val;

@EqualsAndHashCode(callSuper = true)
public class MovementVar extends Variable {

  private int k;
  private int time;

  public MovementVar(int k, int t) {
    this.time = t;
    this.k = k;
  }

  @Override
  public Variable copy() {
    val mov = new MovementVar(k, time);
    if (isNegated()) {
      return mov.negated();
    } else {
      return mov;
    }
  }

  @Override
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "MovementVar(k=" + k + ", t=" + time + ')';
  }

}
