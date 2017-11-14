package sat.variables;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClauseFormula {
  private Stream<VarClause> clauses;

  public ClauseFormula() {
    clauses = Stream.empty();
  }

  public ClauseFormula(Stream<VarClause> clauses) {
    this.clauses = clauses;
  }

  public ClauseFormula(VarClause... vars) {
    this(Arrays.stream(vars));
  }

  public Stream<VarClause> getClauses() {
    return clauses;
  }

  public static ClauseFormula concat(ClauseFormula form1, ClauseFormula form2) {
    return new ClauseFormula(Stream.concat(form1.clauses, form2.clauses));
  }

  public static ClauseFormula empty() {
    return new ClauseFormula();
  }

  public void addClause(Stream<VarClause> newClauses) {
    clauses = Stream.concat(clauses, newClauses);
  }

  @Override
  public String toString() {
    return clauses.map(VarClause::toString)
        .collect(Collectors.joining("\n"));
  }

  public String toStringWithNames() {
    return clauses.map(VarClause::toStringWithNames)
        .collect(Collectors.joining("\n"));
  }

  public Stream<String> getStringLines() {
    return clauses.map(VarClause::toStringWithNames);
  }


}
