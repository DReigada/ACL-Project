package table;

public class Cell {
    public enum WallPosition {
        Left, Right, Up, Down
    }

    public final int i, j;

    private boolean[] walls;

    public Cell(int i, int j) {
        this.i = i;
        this.j = j;
        walls = new boolean[4];
    }

    public void addWall(WallPosition pos) {
        switch (pos) {
            case Left:
                walls[0] = true;
                break;
            case Right:
                walls[1] = true;
                break;
            case Up:
                walls[2] = true;
                break;
            case Down:
                walls[3] = true;
                break;
            default:
                throw new RuntimeException("Invalid WallPosition this should never happen");
        }
    }

    public boolean hasWall(WallPosition pos) {
        switch (pos) {
            case Left:
                return walls[0];
            case Right:
                return walls[1];
            case Up:
                return walls[2];
            case Down:
                return walls[3];
            default:
                throw new RuntimeException("Invalid WallPosition this should never happen");
        }
    }


}
