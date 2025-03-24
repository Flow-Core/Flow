package flow;

public class Bool extends Thing {
    public final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    public Bool(int value) {
        this.value = value > 0;
    }

    public Bool not() {
        return new Bool(!value);
    }

    public Bool and(Bool other) {
        return new Bool(this.value && other.value);
    }

    public Bool or(Bool other) {
        return new Bool(this.value || other.value);
    }

    public static Bool fromPrimitive(boolean value) {
        return new Bool(value);
    }

    @Override
    public boolean equals(Thing other) {
        if (other instanceof Bool) {
            return this.value == ((Bool) other).value;
        }

        return false;
    }

    public boolean notEquals(Thing other) {
        return !this.equals(other);
    }

    @Override
    public int hash() {
        return Boolean.hashCode(value);
    }

    @Override
    public String string() {
        return new String(value ? "true" : "false");
    }

    @Override
    public Bool copy() {
        return new Bool(value);
    }
}
