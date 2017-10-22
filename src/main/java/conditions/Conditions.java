package conditions;

import lombok.val;
import table.Table;
import variables.ClauseFormula;
import variables.PositionVar;
import variables.VarClause;

import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Conditions {

    public static ClauseFormula robotMustHavePosition(Table table, int time) {
        int maxPos = table.getSize() * table.getSize();

        Stream<VarClause> clauses = IntStream.range(0, 4)
                .mapToObj(robot -> {
                    val positions = IntStream.rangeClosed(1, maxPos)
                            .mapToObj(j -> new PositionVar(j, robot, time));

                    return new VarClause(positions);
                });

        return new ClauseFormula(clauses);
    }

}
