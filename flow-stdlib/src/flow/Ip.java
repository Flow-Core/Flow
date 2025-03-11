package flow;

public abstract class Ip extends Thing {
    public String value;

    @Override
    public String string() {
        return value.copy();
    }
}
