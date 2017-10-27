package com.dreigada;

import conditions.Conditions;
import lombok.val;
import table.Table;

public class App {
  public static void main(String[] args) {
    val table = new Table(3);
    val c1 = Conditions.robotMustHavePosition(table, 1);
    val c2 = Conditions.robotCanNotHaveTwoPositions(table, 1);
    val c3 = Conditions.onlyOneRobotCanMoveEachTimeStep(1);
    val c4 = Conditions.stopVertex(table, 1);
    val c5 = Conditions.noRobotsBetweenOrigAndDest(table, 1);

    System.out.println(c5.toStringWithNames());


//        table.cells[0][0].addWall(Table.Direction.Right);
//        table.cells[0][1].addWall(Table.Direction.Left);
//
//        table.connectCells();
//
//        val list = table.listEdges().collect(Collectors.toList());
//
//
//
//        list.forEach(System.out::println);

  }
}
