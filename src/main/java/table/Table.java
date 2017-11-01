package table;

import fomatters.IParser;
import lombok.val;

import java.util.stream.Stream;

import static helpers.StreamHelpers.range;

public class Table {
  private ConnectedCell[][] connectedCells;
  private int size;

  public Table(IParser.ParsedInput input) {
    this.size = input.getSize();
    this.connectedCells = new ConnectedCell[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        connectedCells[i][j] = new ConnectedCell(i, j, this);
      }
    }

    connectCells(); // connnect without walls

    for (val wall : input.getWalls()) {
      val dir = wall.getDirection();
      val cell = connectedCells[wall.getI()][wall.getJ()];

      cell.addWall(dir);
      cell.getNext(dir).addWall(dir.getInverse());
    }

    disconnectCells();
    connectCells();  // connnect with walls
  }

  public int getSize() {
    return size;
  }

  public int getMaxPosition() {
    return size * size;
  }

  public void disconnectCells() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        val cell = connectedCells[i][j];
        cell.disconnect();
      }
    }
  }

  public Stream<ConnectedCell> getAllConnectedCells() {
    return range(0, size).flatMap(i ->
        range(0, size).map(j -> connectedCells[i][j]));
  }

  public Stream<EdgeWithDirection> getAllEdges() {
    return getAllConnectedCells().flatMap(ConnectedCell::listAllConnected);
  }

  public int getCellId(int i, int j) {
    return (j + 1) + (i * (getSize()));
  }

  public int[] getCoordsFromId(int id) {
    return new int[]{((id - 1) / size), (id - 1) % size};
  }


  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        str.append(connectedCells[i][j].toString());
        str.append(" ");
      }
      str.append("\n");
    }
    return str.toString();
  }

  private void connectCells() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        val cell = connectedCells[i][j];

        if (i != 0 && !cell.hasWall(Direction.Up)) {
          cell.up = connectedCells[i - 1][j];
        }

        if (i != size - 1 && !cell.hasWall(Direction.Down)) {
          cell.down = connectedCells[i + 1][j];
        }

        if (j != 0 && !cell.hasWall(Direction.Left)) {
          cell.left = connectedCells[i][j - 1];
        }

        if (j != size - 1 && !cell.hasWall(Direction.Right)) {
          cell.right = connectedCells[i][j + 1];
        }

      }
    }
  }


  public enum Direction {
    Left, Right, Up, Down;

    public Direction getInverse() {
      switch (this) {
        case Up:
          return Down;
        case Down:
          return Up;
        case Left:
          return Right;
        case Right:
          return Left;
        default:
          throw new RuntimeException("Invalid Direction this should never happen");
      }
    }
  }
}

