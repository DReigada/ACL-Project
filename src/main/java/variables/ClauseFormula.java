package variables;

import org.sat4j.core.VecInt;
import org.sat4j.specs.IVecInt;

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

  public void addClause(Stream<VarClause> newClauses) {
    clauses = Stream.concat(clauses, newClauses);
  }


  public static ClauseFormula concat(ClauseFormula form1, ClauseFormula form2) {
    return new ClauseFormula(Stream.concat(form1.clauses, form2.clauses));
  }

  public static ClauseFormula empty() {
    return new ClauseFormula();
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

  public Stream<IVecInt> getClausesAsVecInt() {
    return
        clauses
            .map(VarClause::getIds)
            .map(VecInt::new);
  }

}
