package variables;

import lombok.EqualsAndHashCode;

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
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "PossibleMoveVar(" + "j=" + j + ", l=" + l + ", t=" + time + ')';
  }

}
