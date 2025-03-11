package flow;

public final class String extends Thing {
    public final java.lang.String value;

    public String(java.lang.String value) {
        this.value = value;
    }

    public String(String value) {
        this.value = value.value;
    }

    public Int length() {
        return new Int(value.length());
    }

    public String plus(String other) {
        return new String(this.value + other.value);
    }

    public String times(Int times) {
        return new String(java.lang.String.valueOf(this.value).repeat(Math.max(0, times.value)));
    }

    public String get(int index) {
        return new String(Character.toString(this.value.charAt(index)));
    }

    public String toUpperCase() {
        return new String(value.toUpperCase());
    }

    public String substring(Int start, Int end) {
        return new String(value.substring(start.value, end.value));
    }

    public static String fromPrimitive(java.lang.String value) {
        return new String(value);
    }

    public java.lang.String toJavaType() {
        return value;
    }

    public static String fromJavaType(java.lang.String value) {
        return new String(value);
    }

    @Override
    public Bool equals(Thing other) {
        if (other instanceof String) {
            return new Bool(this.value.equals(((String) other).value));
        }
        return new Bool(false);
    }

    public Bool notEquals(Thing other) {
        Bool eq = this.equals(other);
        return new Bool(!eq.value);
    }

    public Bool lessThan(String other) {
        return new Bool(this.value.compareTo(other.value) < 0);
    }

    public Bool greaterThan(String other) {
        return new Bool(this.value.compareTo(other.value) > 0);
    }

    public Bool lessOrEquals(String other) {
        return new Bool(this.value.compareTo(other.value) <= 0);
    }

    public Bool greaterOrEquals(String other) {
        return new Bool(this.value.compareTo(other.value) >= 0);
    }

    @Override
    public String string() {
        return this;
    }

    @Override
    public String copy() {
        return new String(value);
    }
}
