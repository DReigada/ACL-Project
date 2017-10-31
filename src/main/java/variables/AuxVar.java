package variables;

import lombok.EqualsAndHashCode;
import lombok.val;

import java.util.Arrays;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = true)
public class AuxVar extends Variable {
  Variable[] vars;

  /*
  Carefull: the order matters for equality
   */
  public AuxVar(Variable... vars) {
    this.vars = vars;
  }

  @Override
  public Variable copy() {
    return new AuxVar(vars);
  }

  public Stream<VarClause> getImplications() {
    val negated = Arrays.stream(vars)
        .map(Variable::negated);
    val firstClause = new VarClause(negated);
    firstClause.addVariable(this);

    Stream<VarClause> otherClauses = Arrays.stream(vars)
        .map(var -> new VarClause(this.negated(), var));

    return Stream.concat(Stream.of(firstClause), otherClauses);
  }

  @Override
  public String toString() {
    String neg = isNegated() ? "-" : "";
    return neg + "AuxVar{" +
        "vars=" + Arrays.toString(vars) +
        '}';
  }
}
