package variables;

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

    @Override
    public String toString() {
        return clauses.map(VarClause::toString)
                .collect(Collectors.joining("\n"));
    }

}
