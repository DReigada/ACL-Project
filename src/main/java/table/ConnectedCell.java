package table;

import lombok.val;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ConnectedCell {

    public final int i, j;

    ConnectedCell left, right, up, down;

    private Table table;

    public ConnectedCell(int i, int j, Table table) {
        this.i = i;
        this.j = j;
        this.table = table;
    }


    public Stream<EdgeWithDirection> listAllConnected() {
        return Arrays.stream(Table.Direction.values()).flatMap(dir -> listConnected(dir).stream());
    }

    public int getId() {
        return (j + 1) + (i * (table.getSize()));
    }


    private List<EdgeWithDirection> listConnected(Table.Direction dir) {
        ConnectedCell next = getConnected(dir);
        val acc = new LinkedList<EdgeWithDirection>();

        while (next != null) {
            val edge = new EdgeWithDirection(this, next, dir);
            acc.add(edge);
            next = next.getConnected(dir);
        }

        return acc;
    }

    private ConnectedCell getConnected(Table.Direction dir) {
        switch (dir) {
            case Left:
                return left;
            case Right:
                return right;
            case Up:
                return up;
            case Down:
                return down;
            default:
                throw new RuntimeException("Invalid WallPosition this should never happen");
        }
    }

    @Override
    public String toString() {
        return "{" +
                "i=" + i +
                ", j=" + j +
                ", id=" + getId() +
                '}';
    }
}
