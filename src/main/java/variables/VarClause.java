package variables;

import lombok.Value;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value
public class VarClause {
    private Stream<? extends Variable> vars;

    public VarClause(Stream<? extends Variable> vars) {
        this.vars = vars;
    }

    public VarClause(Variable... vars) {
        this(Arrays.stream(vars));
    }


    @Override
    public String toString() {
        return vars.map(Variable::id)
                .map(Object::toString)
                .collect(Collectors.joining(" "));
    }

    public String toStringWithNames(){
        return vars.map(Variable::toString)
                .collect(Collectors.joining(" "));
    }
}
