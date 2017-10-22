package table;

public class Table {
    private Cell[][] cells;
    private int size;

    public Table(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }


    public int getSize() {
        return size;
    }

    public int getMaxPosition() {
        return size * size;
    }

}

