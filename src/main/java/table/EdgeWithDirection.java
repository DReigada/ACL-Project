package table;

import lombok.Value;

@Value
public class EdgeWithDirection {
    ConnectedCell orig, dest;
    Table.Direction direction;
}
