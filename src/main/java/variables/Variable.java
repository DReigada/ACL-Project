package variables;

import lombok.EqualsAndHashCode;
import lombok.val;

@EqualsAndHashCode(exclude = {"negated"})
public abstract class Variable {

  private int time;

  private boolean negated;

  protected Variable(int time) {
    negated = false;
    this.time = time;
  }

  public int id() {
    Integer repr = VarMap.map.get(this);
    if (repr == null) {
      repr = VarMap.map.size() + 1;
      VarMap.map.put(this, repr);
    }
    if (isNegated()) {
      return -repr;
    } else {
      return repr;
    }
  }

  public VarClause implies(VarClause clause) {
    val a = this.negate();
    clause.addVariable(a);
    return clause;
  }

  public VarClause implies(Variable var) {
    val a = this.negate();
    return new VarClause(a, var);
  }

  /**
   * Careful: This changes the state of this object, even tough it returns it
   */
  public Variable negate() {
    this.negated = true;
    return this;
  }

  public int getTime() {
    return time;
  }

  public boolean isNegated() {
    return negated;
  }
}

