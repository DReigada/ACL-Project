package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AuxVar extends Variable {
  int[] ids;

  public AuxVar(int... ids) {
    this.ids = ids;
  }
}
