package flow;

@FunctionalInterface
public interface Consumer2<P1, P2> {
    void invoke(P1 param1, P2 param2);
}
