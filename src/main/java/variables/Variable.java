package variables;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
abstract class Variable {

    private int time;

    protected Variable(int time) {
        this.time = time;
    }

    public int repr() {
        Integer repr = VarMap.map.get(this);
        if (repr == null) {
            repr = VarMap.map.size() + 1;
            VarMap.map.put(this, repr);
        }
        return repr;
    }

    public int getTime() {
        return time;
    }
}

