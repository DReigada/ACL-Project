package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MovementVar extends Variable {

  private int k;
  private int time;

  public MovementVar(int k, int t) {
    this.time = t;
    this.k = k;
  }

  @Override
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "MovementVar(k=" + k + ", t=" + time + ')';
  }

}
