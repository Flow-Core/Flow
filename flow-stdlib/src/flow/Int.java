package flow;

public final class Int extends Thing {
    public int value;

    public Int(int value) {
        this.value = value;
    }

    // Arithmetic operators (using primitives)
    public int plus(int other) {
        return this.value + other;
    }

    public int minus(int other) {
        return this.value - other;
    }

    public int times(int other) {
        return this.value * other;
    }

    public int div(int other) {
        return this.value / other;
    }

    public int mod(int other) {
        return this.value % other;
    }

    // Bitwise operators (using primitives)
    public int and(int other) {
        return this.value & other;
    }

    public int or(int other) {
        return this.value | other;
    }

    public int not() {
        return ~this.value;
    }

    // Comparison operators (returning primitive booleans)
    public boolean lessThan(int other) {
        return this.value < other;
    }

    public boolean greaterThan(int other) {
        return this.value > other;
    }

    public boolean lessOrEquals(int other) {
        return this.value <= other;
    }

    public boolean greaterOrEquals(int other) {
        return this.value >= other;
    }

    // Equality operators (primitive comparisons)
    public boolean equals(int other) {
        return this.value == other;
    }

    public boolean notEquals(int other) {
        return this.value != other;
    }

    // Increment and decrement operators modify the internal state
    public int postInc() {
        return this.value++;
    }

    public int postDec() {
        return this.value--;
    }

    public int preInc() {
        return ++this.value;
    }

    public int preDec() {
        return --this.value;
    }

    public int pos() {
        return value;
    }

    public int neg() {
        return value * -1;
    }

    public static Int fromPrimitive(int value) {
        return new Int(value);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    // Overriding string() to return a java.lang.String representation
    @Override
    public String string() {
        return new String(java.lang.String.valueOf(value));
    }

    // Creates a copy of this Int wrapper
    @Override
    public Int copy() {
        return new Int(value);
    }
}
