package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PossibleMoveVar extends Variable {

    private int j, l;

    public PossibleMoveVar(int j, int l, int t) {
        super(t);
        this.j = j;
        this.l = l;
    }

    @Override
    public String toString() {
        return "PossibleMoveVar(" + "j=" + j + ", l=" + l + ", t=" + getTime() + ')';
    }

}
