package flow.collections;

import flow.*;

import java.util.ArrayList;

public class List<T> implements Collection<T> {
    java.util.List<T> _list;

    public List() {
        _list = new ArrayList<>();
    }

    public List(java.util.List<T> list) {
        _list = list;
    }

    public List<T> filter(Function1<T, Bool> delegate) {
        List<T> out = new List<>();

        for (T e : _list) {
            if (delegate.invoke(e).value)
                out.add(e);
        }

        return out;
    }

    public T find(Function1<T, Bool> delegate) {
        for (T e : _list) {
            if (delegate.invoke(e).value)
                return e;
        }

        return null;
    }

    public T findLast(Function1<T, Bool> delegate) {
        T last = null;

        for (T e : _list) {
            if (delegate.invoke(e).value)
                last = e;
        }

        return last;
    }

    public T get(int i) {
        return _list.get(i);
    }

    public void set(int i, T value) {
        _list.set(i, value);
    }

    public boolean any(Function1<T, Bool> delegate) {
        for (T e : _list) {
            if (delegate.invoke(e).value)
                return true;
        }

        return false;
    }

    public boolean all(Function1<T, Bool> delegate) {
        for (T e : _list) {
            if (!delegate.invoke(e).value)
                return false;
        }

        return true;
    }

    public <R> List<R> map(Function1<T, R> delegate) {
        List<R> out = new List<>();

        for (T e : _list) {
            out.add(delegate.invoke(e));
        }

        return out;
    }

    @SuppressWarnings("Comparator")
    public void sort(Function2<T, T, Bool> delegate) {
        _list.sort((e1, e2) -> {
            if (delegate.invoke(e1, e2).value) return 1;  // e1 > e2
            if (delegate.invoke(e2, e1).value) return -1; // e1 < e2
            return 0;
        });
    }

    public List<T> sorted(Function2<T, T, Bool> delegate) {
        return new List<>(
            _list.stream().sorted((e1, e2) -> {
                if (delegate.invoke(e1, e2).value) return 1;  // e1 > e2
                if (delegate.invoke(e2, e1).value) return -1; // e1 < e2
                return 0;
            }).toList()
        );
    }

    @Override
    public int count() {
        return _list.size();
    }

    @Override
    public boolean empty() {
        return _list.isEmpty();
    }

    @Override
    public boolean add(T e) {
        return _list.add(e);
    }

    @Override
    public void add(int i, T e) {
        _list.add(i, e);
    }

    @Override
    public boolean remove(T e) {
        return _list.remove(e);
    }

    @Override
    public void pop(int i) {
        _list.remove(i);
    }

    @Override
    public void pop() {
        _list.remove(_list.size() - 1);
    }

    @Override
    public void addAll(Collection<T> c) {
        c.forEach(e -> _list.add(e));
    }

    @Override
    public void removeAll(Collection<T> c) {
        c.forEach(e -> _list.remove(e));
    }

    @Override
    public void clear() {
        _list.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return new ListIterator(0);
    }

    @Override
    public void forEachIndexed(Consumer2<Int, T> delegate) {
        ListIterator it = new ListIterator(0);

        while (it.hasNext()) {
            delegate.invoke(new Int(it.cursor), it.next());
        }
    }

    private class ListIterator implements Iterator<T> {
        int cursor;
        int lastRet = -1;

        ListIterator(int index) {
            cursor = index;
        }

        @Override
        public T next() {
            // TODO: Exceptions
            lastRet = cursor++;

            return _list.get(lastRet);
        }

        @Override
        public boolean hasNext() {
            return cursor != _list.size();
        }
    }
}
