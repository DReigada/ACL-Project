package variables;

import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
        return "PositionVar(" + "j=" + j + ", k=" + k + ", t=" + getTime() + ')';
    }

}
