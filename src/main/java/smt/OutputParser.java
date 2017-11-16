package smt;

import fomatters.IParser;
import lombok.val;
import sat.solver.AbstractSolver;
import table.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class OutputParser {
  private final static Pattern movementRegex =
      Pattern.compile("\\(RobotMovement (.*) \\(Movement \\(Position (\\d*)\\) \\(Position (\\d*)\\)\\)\\)");

  private Table table;
  private BufferedReader reader;

  public OutputParser(Table table, BufferedReader reader) {
    this.table = table;
    this.reader = reader;
  }

//  public Optional<Stream<AbstractSolver.Move>> parse() throws IOException {
//    if (isSat()) {
//      return Optional.of(parseMoves(numMoves.get()));
//    } else {
//      return Optional.empty();
//    }
//  }


//  public Optional<Integer> isSat() throws IOException {
//    if (reader.readLine().equals("sat")) {
//      reader.readLine();
//      val obj = reader.readLine();
//      reader.readLine();
//
//      val regex = Pattern.compile("\\(usedTime (\\d*)\\)");
//      Matcher matcher = regex.matcher(obj);
//      matcher.find();
//      int time = Integer.parseInt(matcher.group(1));
//      return Optional.of(time);
//    } else {
//      return Optional.empty();
//    }
//  }

  public boolean isSat() throws IOException {
    return reader.readLine().equals("sat");
  }

  public Stream<AbstractSolver.Move> parseMoves(int linesToRead) throws IOException {
    val list = new LinkedList<AbstractSolver.Move>();

    for (int i = 0; i < linesToRead; i++) {
      list.add(parseMove(reader.readLine()));
    }

    return list.stream();
  }

  private AbstractSolver.Move parseMove(String line) {
    val matcher = movementRegex.matcher(line);
    matcher.find();
    val robot = IParser.Robot.valueOf(matcher.group(1));
    val orig = Integer.parseInt(matcher.group(2));
    val dest = Integer.parseInt(matcher.group(3));

    return new AbstractSolver.Move(robot.toId(), -1, table.directionFromCoords(orig, dest));
  }
}
