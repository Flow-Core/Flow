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
    public Bool equals(Thing other) {
        if (other instanceof Bool) {
            return new Bool(this.value == ((Bool) other).value);
        }
        return new Bool(false);
    }

    public Bool notEquals(Thing other) {
        Bool eq = this.equals(other);
        return new Bool(!eq.value);
    }

    @Override
    public Int hash() {
        return new Int(Boolean.hashCode(value));
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
