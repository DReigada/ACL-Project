package table;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ConnectedCell {

  public final int i, j;

  ConnectedCell left, right, up, down;

  private Table table;

  private boolean leftWall, rightWall, upWall, downWall;

  public ConnectedCell(int i, int j, Table table) {
    this.i = i;
    this.j = j;
    this.table = table;
  }


  public void addWall(Table.Direction pos) {
    switch (pos) {
      case Left:
        leftWall = true;
        break;
      case Right:
        rightWall = true;
        break;
      case Up:
        upWall = true;
        break;
      case Down:
        downWall = true;
        break;
      default:
        throw new RuntimeException("Invalid WallPosition this should never happen");
    }
  }

  public boolean hasWall(Table.Direction pos) {
    switch (pos) {
      case Left:
        return leftWall;
      case Right:
        return rightWall;
      case Up:
        return upWall;
      case Down:
        return downWall;
      default:
        throw new RuntimeException("Invalid WallPosition this should never happen");
    }
  }

  public void disconnect() {
    left = null;
    right = null;
    up = null;
    down = null;
  }


  public Stream<EdgeWithDirection> listAllConnected() {
    return Arrays.stream(Table.Direction.values())
        .flatMap(dir -> listConnected(dir).stream());
  }

  public int getId() {
    return table.getCellId(i, j);
  }

  public ConnectedCell getNext(Table.Direction dir) {
    return getNextOpt(dir).orElse(null);
  }

  public Optional<ConnectedCell> getNextOpt(Table.Direction dir) {
    switch (dir) {
      case Up:
        return Optional.ofNullable(up);
      case Down:
        return Optional.ofNullable(down);
      case Left:
        return Optional.ofNullable(left);
      case Right:
        return Optional.ofNullable(right);
      default:
        throw new RuntimeException("Invalid WallPosition this should never happen");
    }
  }

  private List<EdgeWithDirection> listConnected(Table.Direction dir) {
    ConnectedCell next = getNext(dir);
    val acc = new LinkedList<EdgeWithDirection>();

    while (next != null) {
      val newNext = next.getNext(dir);
      val isLast = newNext == null;
      val edge = new EdgeWithDirection(this, next, dir, isLast);
      acc.add(edge);
      next = newNext;
    }

    return acc;
  }

  @Override
  public String toString() {
    return "ConnectedCell{" +
        "id=" + getId() +
        ", left=" + leftWall +
        ", right=" + rightWall +
        ", up=" + upWall +
        ", down=" + downWall +
        '}';
  }
}
