package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class Variable {

    private int time;

    private boolean neg;

    protected Variable(int time) {
        neg = false;
        this.time = time;
    }

    public int id() {
        Integer repr = VarMap.map.get(this);
        if (repr == null) {
            repr = VarMap.map.size() + 1;
            VarMap.map.put(this, repr);
        }
        return repr;
    }

    /**
     * Careful: This changes the state of this object, even tough it returns it
     */
    public Variable negate() {
        this.neg = true;
        return this;
    }

    public int getTime() {
        return time;
    }
}

