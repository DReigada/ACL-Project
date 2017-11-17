package smt;

import fomatters.IParser;
import lombok.val;
import table.Table;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static smt.MovementConditionsGenerator.generateMovementConditions;
import static smt.PositionConditionsGenerator.*;

public class SmtFileGenerator {

  private static final String possibleMovementsExpr = exprQuoted("possibleMovements");
  private static final String maxPositionExpr = exprQuoted("maxPosition");
  private static final String initialPositionsExpr = exprQuoted("initialPositions");
  private static final String objectivePositionExpr = exprQuoted("objectivePosition");

  public final Table table;
  private final IParser.ParsedInput input;


  public SmtFileGenerator(IParser.ParsedInput input) {
    this.input = input;
    this.table = new Table(input);
  }

  public void generateAndSave(Path path) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(getMainTemplateFile()))) {
      val newLines = replaceLines(reader.lines());
      Files.write(path, (Iterable<String>) newLines::iterator);
    }
  }


  public String generateStepConditions(InputStream templateFile, int time) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(templateFile))) {
      Stream<String> newLines =
          reader.lines()
              .map(a -> replaceStepConditionLine(a, time, time + 1));

      return newLines.collect(Collectors.joining("\n", "\n", "\n"));
    }
  }

  private String replaceStepConditionLine(String str, int t0, int t1) {
    return str
        .replace("$0", Integer.toString(t0))
        .replace("$1", Integer.toString(t1));
  }


  private Stream<String> replaceLines(Stream<String> lines) throws IOException {
    return lines.flatMap(this::replaceIfMatches);
  }

  public String replaceIfMatchesObjective(String line, int step) {
    if (line.contains(objectivePositionExpr)) return generateObjectivePosition(input, table, Integer.toString(step));
    else return line;
  }

  private InputStream getMainTemplateFile() {
    return getClass().getResourceAsStream("/MainTemplate.smt");
  }

  public InputStream getCheckStepTemplate() {
    return getClass().getResourceAsStream("/CheckSatTemplate.smt");
  }


  public InputStream getStepTemplateFile() {
    return getClass().getResourceAsStream("/StepsTemplate.smt");
  }


  private Stream<String> replaceIfMatches(String line) {
    if (line.contains(possibleMovementsExpr)) return generateMovementConditions(table);
    else if (line.contains(maxPositionExpr)) return generateMaxPosition(table);
    else if (line.contains(initialPositionsExpr)) return generateInitialPositions(input, table);
//    else if (line.contains(objectivePositionExpr)) return generateObjectivePosition(input, table, USEd);
    else return Stream.of(line);
  }


  private static final String exprQuoted(String expr) {
    return ";;;{" + expr + "}";
  }

}
