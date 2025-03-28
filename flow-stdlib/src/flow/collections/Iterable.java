package flow.collections;

import flow.*;

import java.lang.Void;
import java.util.function.Function;

public interface Iterable<T> {
    Iterator<T> iterator();

    default void forEach(Consumer1<T> delegate) {
        Iterator<T> it = iterator();

        while (it.hasNext()) {
            delegate.invoke(it.next());
        }
    }

    void forEachIndexed(Consumer2<Int, T> delegate);
}
