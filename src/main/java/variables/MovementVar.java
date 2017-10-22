package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MovementVar extends Variable {

    private int k;

    public MovementVar(int k, int t) {
        super(t);
        this.k = k;
    }

    @Override
    public String toString() {
        String neg = isNegated() ? "-" : "";
        return neg + "MovementVar(k=" + k + ", t=" + getTime() + ')';
    }

}
