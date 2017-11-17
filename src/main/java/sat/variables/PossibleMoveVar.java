package sat.variables;

import lombok.EqualsAndHashCode;
import lombok.val;

@EqualsAndHashCode(callSuper = true)
public class PossibleMoveVar extends Variable {

  private int j, l;
  private int time;

  public PossibleMoveVar(int j, int l, int t) {
    this.time = t;
    this.j = j;
    this.l = l;
  }

  @Override
  public Variable copy() {
    val mov = new PossibleMoveVar(j, l, time);
    if (isNegated()) {
      return mov.negated();
    } else {
      return mov;
    }
  }

  @Override
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "PossibleMoveVar(" + "j=" + j + ", l=" + l + ", t=" + time + ')';
  }

}
