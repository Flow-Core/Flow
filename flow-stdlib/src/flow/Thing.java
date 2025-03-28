package flow;

public abstract class Thing {
    public Bool equals(Thing other) {
        return new Bool(this == other);
    }

    public Int hash() {
        return new Int(System.identityHashCode(this));
    }

    public String string() {
        return new String(type() + "@" + hash().value);
    }

    public String type() {
        return new String(this.getClass().getSimpleName());
    }

    public Thing copy() {
        throw new UnsupportedOperationException("Copy is not implemented");
    }

    @Override
    public java.lang.String toString() {
        return string().value;
    }
}

