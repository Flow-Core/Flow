package flow;

public final class Void extends Thing {
    private Void() {}

    @Override
    public String string() {
        return new String("Void");
    }
}
