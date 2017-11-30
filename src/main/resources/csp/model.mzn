include "alldifferent.mzn";

int: maxSteps;
int: tableSize;

int: RedPositionRow;
int: RedPositionCol;

int: YellowPositionRow;
int: YellowPositionCol;

int: GreenPositionRow;
int: GreenPositionCol;

int: BluePositionRow;
int: BluePositionCol;

int: objectivePositionRow;
int: objectivePositionCol;

int: objectiveRobot;

set of int: validPositionsSet = 1..tableSize;
set of int: allTimeStepsSet = 0..maxSteps;
set of int: allStepsSetExceptLast = 0..(maxSteps - 1);
set of int: robotsSet = 1..4;

array[validPositionsSet, validPositionsSet, 0..1, 1..tableSize, 0..1] of bool : possibleMoves;

array[allTimeStepsSet, robotsSet, 0..1] of var validPositionsSet : positions; % 0 -> Col, 1 -> Row
var 0..maxSteps: stepsUsed;

var set of int: timeStepsSet = 0..maxSteps;
var set of int: stepsSetExceptLast = 0..(maxSteps - 1);


predicate isPositionFilled(var int: step, var int: positionRow, var int: positionCol) =
  exists (robot in robotsSet) (positions[step, robot, 0] = positionRow /\ positions[step, robot, 1] = positionCol);


predicate isPossible(var int: origRow, var int: origCol, var 0..1: rowOrCol, var int: destCol) = possibleMoves[origRow, origCol, rowOrCol, destCol, 0];
predicate isFinal(var int: origRow, var int: origCol, var 0..1: rowOrCol, var int: destCol) = possibleMoves[origRow, origCol, rowOrCol, destCol, 1];


predicate DO_ROW_STUFF(var int: origRow, var int: origCol, var int: destRow, var int: destCol, var int: step) =
  isPossible(origRow, origCol, 0, destCol)
  /\ (
    if origCol < destCol then
      forall (col in (origCol + 1)..destCol) (not isPositionFilled(step, destRow, col))
      /\ (isFinal(origRow, origCol, 0, destCol) \/ isPositionFilled(step, destRow, destCol + 1))
    else
      forall (col in destCol..(origCol - 1)) (not isPositionFilled(step, destRow, col))
      /\ (isFinal(origRow, origCol, 0, destCol) \/ isPositionFilled(step, destRow, destCol - 1))
    endif
  );

predicate DO_COL_STUFF(var int: origRow, var int: origCol, var int: destRow, var int: destCol, var int: step) =
  isPossible(origRow, origCol, 1, destRow)
  /\ (
    if origRow < destRow then
      forall (row in (origRow + 1)..destRow) (not isPositionFilled(step, row, destCol))
      /\ (isFinal(origRow, origCol, 1, destRow) \/ isPositionFilled(step, destRow + 1, destCol))
    else
      forall (row in destRow..(origRow - 1)) (not isPositionFilled(step, row, destCol))
      /\ (isFinal(origRow, origCol, 1, destRow) \/ isPositionFilled(step, destRow - 1, destCol))
    endif
  );


predicate isPossibleMovement(var int: origRow, var int: origCol, var int: destRow, var int: destCol, var int: step) =
  ((origRow = destRow) /\ DO_ROW_STUFF(origRow, origCol, destRow, destCol, step))
  \/ ((origCol = destCol) /\ DO_COL_STUFF(origRow, origCol, destRow, destCol, step));


%%%%% constraints

%% initial and objective

constraint positions[0, 1, 0] = RedPositionRow;
constraint positions[0, 1, 1] = RedPositionCol;

constraint positions[0, 2, 0] = YellowPositionRow;
constraint positions[0, 2, 1] = YellowPositionCol;

constraint positions[0, 3, 0] = GreenPositionRow;
constraint positions[0, 3, 1] = GreenPositionCol;

constraint positions[0, 4, 0] = BluePositionRow;
constraint positions[0, 4, 1] = BluePositionCol;

constraint positions[stepsUsed, objectiveRobot, 0] = objectivePositionRow;
constraint positions[stepsUsed, objectiveRobot, 1] = objectivePositionCol;

%%%%%

%% all positions must be different
constraint forall(timeStep in timeStepsSet, robot in robotsSet)(
  forall(robot2 in robotsSet where robot2 != robot) (
    not (
      (positions[timeStep, robot, 0] = positions[timeStep, robot2, 0])
      /\ (positions[timeStep, robot, 1] = positions[timeStep, robot2, 1])
    )
  )
);

% if a robot moved then its movement must be valid and no other robot should move
constraint forall(timeStep in stepsSetExceptLast, robot in robotsSet) (
  ((positions[timeStep, robot, 0] = positions[timeStep + 1, robot, 0]) /\ (positions[timeStep, robot, 1] = positions[timeStep + 1, robot, 1]))
  \/ (
    isPossibleMovement(positions[timeStep, robot, 0], positions[timeStep, robot, 1], positions[timeStep + 1, robot, 0], positions[timeStep + 1, robot, 1], timeStep)
    /\ forall(robot2 in robotsSet where robot2 != robot) (
      positions[timeStep, robot2, 0] = positions[timeStep + 1, robot2, 0] /\ positions[timeStep, robot2, 1] = positions[timeStep + 1, robot2, 1])
  )
);


solve satisfy;
% solve minimize stepsUsed;


output [show(stepsUsed)];
output ["\n"];

output ["positions r: "];
output [show(positions[i, 1, 0]) ++ "," ++ show(positions[i, 1, 1]) ++ "->" | i in allTimeStepsSet];
output ["\n"];

output ["positions y: "];
output [show(positions[i, 2, 0]) ++ "," ++ show(positions[i, 2, 1]) ++ "->" | i in allTimeStepsSet];
output ["\n"];

output ["positions g: "];
output [show(positions[i, 3, 0]) ++ "," ++ show(positions[i, 3, 1]) ++ "->" | i in allTimeStepsSet];
output ["\n"];

output ["positions b: "];
output [show(positions[i, 4, 0]) ++ "," ++ show(positions[i, 4, 1]) ++ "->" | i in allTimeStepsSet];
output ["\n"];
