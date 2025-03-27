package flow;

import flow.collections.Array;
import flow.collections.ByteArray;

import java.util.regex.Pattern;

public final class String extends Thing {
    public final java.lang.String value;

    public String(java.lang.String value) {
        this.value = value;
    }

    public String(String value) {
        this.value = value.value;
    }

    public int length() {
        return value.length();
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

    public int toInt() {
        return Integer.parseInt(value);
    }

    public float toFloat() {
        return Float.parseFloat(value);
    }

    public ByteArray toByteArray() {
        return new ByteArray(value.getBytes());
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

    public Array<String> split(String delimiter) {
        java.lang.String[] substrings = value.split(Pattern.quote(delimiter.value));

        Array<String> out = new Array<>(substrings.length);

        for (int i = 0; i < substrings.length; i++) {
            out.set(i, new String(substrings[i]));
        }

        return out;
    }

    @Override
    public boolean equals(Thing other) {
        if (other instanceof String) {
            return this.value.equals(((String) other).value);
        }

        return false;
    }

    public boolean notEquals(Thing other) {
        return !this.equals(other);
    }

    public boolean lessThan(String other) {
        return this.value.compareTo(other.value) < 0;
    }

    public boolean greaterThan(String other) {
        return this.value.compareTo(other.value) > 0;
    }

    public boolean lessOrEquals(String other) {
        return this.value.compareTo(other.value) <= 0;
    }

    public boolean greaterOrEquals(String other) {
        return this.value.compareTo(other.value) >= 0;
    }

    @Override
    public int hash() {
        return value.hashCode();
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
