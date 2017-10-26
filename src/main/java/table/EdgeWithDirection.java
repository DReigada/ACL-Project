package table;

import lombok.Value;

@Value
public class EdgeWithDirection {
    ConnectedCell orig, dest;
    Table.Direction direction;
    boolean isLast;

    public int getNextId() {
        return dest.getNextId(direction);
    }
}
