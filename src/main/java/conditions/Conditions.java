package conditions;

import lombok.val;
import table.Table;
import variables.ClauseFormula;
import variables.MovementVar;
import variables.PositionVar;
import variables.VarClause;

import java.util.stream.IntStream;
import java.util.stream.Stream;


public class Conditions {
    public static int NUM_ROBOTS = 4;

    public static ClauseFormula robotMustHavePosition(Table table, int time) {
        int maxPos = table.getMaxPosition();

        Stream<VarClause> clauses =
                robotRange()
                        .map(robot -> {
                            val positions = IntStream.rangeClosed(1, maxPos)
                                    .mapToObj(j -> new PositionVar(j, robot, time));
                            return new VarClause(positions);
                        });

        return new ClauseFormula(clauses);
    }

    public static ClauseFormula robotCanNotHaveTwoPositions(Table table, int time) {
        int maxPos = table.getMaxPosition();

        Stream<VarClause> clauses =
                robotRange()
                        .flatMap(robot ->
                                rangeClosed(1, maxPos).flatMap(j ->
                                        rangeClosed(j + 1, maxPos).map(l -> {
                                            val var1 = new PositionVar(j, robot, time).negate();
                                            val var2 = new PositionVar(l, robot, time).negate();
                                            return new VarClause(var1, var2);
                                        })
                                )
                        );

        return new ClauseFormula(clauses);
    }

    public static ClauseFormula onlyOneRobotCanMoveEachTimeStep(int time) {
        Stream<VarClause> clauses = robotRange()
                .flatMap(k ->
                        range(k + 1, NUM_ROBOTS).map(h -> {
                            val var1 = new MovementVar(k, time).negate();
                            val var2 = new MovementVar(h, time).negate();
                            return new VarClause(var1, var2);
                        })
                );

        return new ClauseFormula(clauses);

    }


    private static Stream<Integer> robotRange() {
        return range(0, NUM_ROBOTS);
    }

    private static Stream<Integer> range(int i, int f) {
        return IntStream.range(i, f).boxed();
    }

    private static Stream<Integer> rangeClosed(int i, int f) {
        return IntStream.rangeClosed(i, f).boxed();
    }

}
