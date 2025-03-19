package flow;

@FunctionalInterface
public interface Function1<P1, Ret> {
    Ret invoke(P1 param1);
}
