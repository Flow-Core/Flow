package flow;

@FunctionalInterface
public interface Function2<P1, P2, Ret> {
    Ret invoke(P1 param1, P2 param2);
}
