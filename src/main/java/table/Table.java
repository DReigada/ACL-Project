package table;

import lombok.val;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Table {
    public enum Direction {
        Left, Right, Up, Down
    }

    public Cell[][] cells;
    public ConnectedCell[][] connectedCells;
    private int size;

    public Table(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        this.connectedCells = new ConnectedCell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell(i, j);
                connectedCells[i][j] = new ConnectedCell(i, j, this);
            }
        }
        connectCells();
    }


    public int getSize() {
        return size;
    }

    public int getMaxPosition() {
        return size * size;
    }

    public void connectCells() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                val cell = cells[i][j];
                val conCell = connectedCells[i][j];

                if (i != 0 && !cell.hasWall(Direction.Up)) {
                    conCell.up = connectedCells[i - 1][j];
                }

                if (i != size - 1 && !cell.hasWall(Direction.Down)) {
                    conCell.down = connectedCells[i + 1][j];
                }

                if (j != 0 && !cell.hasWall(Direction.Left)) {
                    conCell.left = connectedCells[i][j - 1];
                }

                if (j != size - 1 && !cell.hasWall(Direction.Right)) {
                    conCell.right = connectedCells[i][j + 1];
                }

            }
        }
    }

    public Stream<EdgeWithDirection> getAllEdges() {
        return listEdges();
    }

    private Stream<EdgeWithDirection> listEdges() {
        return range(0, size).flatMap(i ->
                range(0, size).flatMap(j ->
                        connectedCells[i][j].listAllConnected()
                ));
    }

    private static Stream<Integer> range(int i, int f) {
        return IntStream.range(i, f).boxed();
    }

}

