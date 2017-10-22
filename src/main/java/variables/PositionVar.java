package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PositionVar extends Variable {

    private int j, k;

    public PositionVar(int j, int k, int t) {
        super(t);
        this.j = j;
        this.k = k;
    }

    @Override
    public String toString() {
        String neg = isNegated() ? "-" : "";
        return neg + "PositionVar(" + "j=" + j + ", k=" + k + ", t=" + getTime() + ')';
    }

}
