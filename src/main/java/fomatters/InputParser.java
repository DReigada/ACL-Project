package fomatters;

import lombok.val;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputParser extends IParser {

  private BufferedReader reader;

  Pattern positionsRegex = Pattern.compile("([RYGB]) (\\d*) (\\d*)");
  Pattern wallsRegex = Pattern.compile("(\\d*) (\\d*) ([udlr])");

  public InputParser(File file) throws FileNotFoundException {
    val fileReader = new FileReader(file);
    reader = new BufferedReader(fileReader);
  }

  public ParsedInput parse() throws IOException {
    val size = Integer.parseInt(reader.readLine());

    List<Position> startingPositions = new ArrayList<>(4);
    for (int i = 0; i < 4; i++) {
      startingPositions.add(parsePosition(reader.readLine()));
    }

    val objective = parsePosition(reader.readLine());

    val wallsNumber = Integer.parseInt(reader.readLine());

    List<Wall> wallsPositions = new ArrayList<>(wallsNumber);
    for (int i = 0; i < wallsNumber; i++) {
      wallsPositions.add(parseWall(reader.readLine()));
    }

    reader.close();
    return new ParsedInput(size, startingPositions, objective, wallsPositions);
  }

  private Position parsePosition(String line) {
    Matcher matcher = positionsRegex.matcher(line);
    matcher.find();
    val robot = Robot.fromString(matcher.group(1));
    val i = parseIndex(matcher.group(2));
    val j = parseIndex(matcher.group(3));

    return new Position(robot, i, j);
  }

  private Wall parseWall(String line) {
    Matcher matcher = wallsRegex.matcher(line);
    matcher.find();
    val i = parseIndex(matcher.group(1));
    val j = parseIndex(matcher.group(2));
    val direction = directionFromString(matcher.group(3));

    return new Wall(i, j, direction);
  }

  private int parseIndex(String str) {
    return Integer.parseInt(str) - 1;
  }

}
