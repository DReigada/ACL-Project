package com.dreigada;

import conditions.Conditions;
import lombok.val;
import table.Table;

public class App {
    public static void main(String[] args) {
        val table = new Table(3);
        val c1 = Conditions.robotMustHavePosition(table, 1);
        val c2 = Conditions.robotCanNotHaveTwoPositions(table, 1);

        System.out.println(c2.toStringWithNames());

    }
}
