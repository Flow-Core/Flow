package flow.collections;

import java.util.Map;

public interface Collection<T> extends Iterable<T> {
    int count();
    boolean empty();

    boolean add(T e);
    void add(int i, T e);
    boolean remove(T e);
    void pop(int i);
    void pop();

    void addAll(Collection<T> c);
    void removeAll(Collection<T> c);

    void clear();
}
