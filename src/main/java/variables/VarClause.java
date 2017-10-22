package variables;

import lombok.Value;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class VarClause {
    private Stream<? extends Variable> vars;

    public VarClause(Stream<? extends Variable> vars) {
        this.vars = vars;
    }


    @Override
    public String toString() {
        return vars.map(Variable::repr)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}
