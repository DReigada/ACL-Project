package table;

import lombok.Value;
import lombok.val;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Value
public class EdgeWithDirection {
  ConnectedCell orig, dest;
  Table.Direction direction;
  boolean isLast;

  public int getNextId() {
    return dest.getNext(direction).getId();
  }

  public Stream<ConnectedCell> getStream() {
    val iter = new Iterator<ConnectedCell>() {
      ConnectedCell currentCell = orig;

      @Override
      public boolean hasNext() {
        return getNext() != null;
      }

      @Override
      public ConnectedCell next() {
        val newCell = getNext();
        currentCell = newCell;
        return newCell;
      }

      private ConnectedCell getNext() {
        if (currentCell.getId() == dest.getId()) {
          return null;
        } else {
          return currentCell.getNext(direction);
        }
      }
    };

    Iterable<ConnectedCell> iterable = () -> iter;

    return StreamSupport.stream(iterable.spliterator(), false);
  }
}
