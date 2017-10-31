package variables;

import lombok.EqualsAndHashCode;
import lombok.val;

@EqualsAndHashCode(exclude = {"negated"})
public abstract class Variable {

  private boolean negated;

  protected Variable() {
    negated = false;
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
    val a = this.negated();
    clause.addVariable(a);
    return clause;
  }

  public VarClause implies(Variable var) {
    val a = this.negated();
    return new VarClause(a, var);
  }

  public Variable negated() {
    val newVar = copy();
    newVar.negated = true;
    return newVar;
  }

  /**
   * Careful: This changes the state of this object, even tough it returns it
   */
  public Variable unNegated() {
    val newVar = copy();
    newVar.negated = false;
    return newVar;
  }

  abstract public Variable copy();

  public boolean isNegated() {
    return negated;
  }
}

