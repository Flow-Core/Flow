package flow;

public abstract class Thing {
    public boolean equals(Thing other) {
        if (other == null) {
            return false;
        }

        return this.hashCode() == other.hashCode() || this == other;
    }

    public int hash() {
        return System.identityHashCode(this);
    }

    public String string() {
        return new String(type() + "@" + hash());
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

