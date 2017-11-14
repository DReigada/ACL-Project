package smt;

import fomatters.IParser;
import lombok.val;
import table.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static smt.MovementConditionsGenerator.generateMovementConditions;
import static smt.PositionConditionsGenerator.*;

public class SmtFileGenerator {

  private static final String possibleMovementsExpr = exprQuoted("possibleMovements");
  private static final String maxPositionExpr = exprQuoted("maxPosition");
  private static final String initialPositionsExpr = exprQuoted("initialPositions");
  private static final String objectivePositionExpr = exprQuoted("objectivePosition");

  private final Table table;
  private final IParser.ParsedInput input;


  public SmtFileGenerator(IParser.ParsedInput input) {
    this.input = input;
    this.table = new Table(input);
  }

  public void generateAndSave(Path path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(getTemplateFile()))) {
      val newLines = replaceLines(reader.lines());
      Files.write(path, (Iterable<String>) newLines::iterator);
    }
  }


  private Stream<String> replaceLines(Stream<String> lines) throws IOException {
    return lines.flatMap(this::replaceIfMatches);
  }

  private Stream<String> replaceIfMatches(String line) {
    if (line.contains(possibleMovementsExpr)) return generateMovementConditions(table);
    else if (line.contains(maxPositionExpr)) return generateMaxPosition(table);
    else if (line.contains(initialPositionsExpr)) return generateInitialPositions(input, table);
    else if (line.contains(objectivePositionExpr)) return generateObjectivePosition(input, table);
    else return Stream.of(line);
  }

  private File getTemplateFile() {
    ClassLoader classLoader = getClass().getClassLoader();
    return new File(classLoader.getResource("template.smt").getFile());
  }


  private static final String exprQuoted(String expr) {
    return ";;;{" + expr + "}";
  }

}
