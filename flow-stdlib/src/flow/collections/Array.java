package flow.collections;

import flow.Consumer1;
import flow.Consumer2;
import flow.Int;

import java.util.Arrays;

public class Array<T> implements Iterable<T> {
    T[] _arr;

    @SuppressWarnings("unchecked")
    public Array(int size) {
        _arr = (T[]) new Object[size];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayIterator(0);
    }

    @Override
    public void forEachIndexed(Consumer2<Int, T> delegate) {
        ArrayIterator it = new ArrayIterator(0);

        while (it.hasNext()) {
            delegate.invoke(new Int(it.cursor), it.next());
        }
    }

    public T get(int index) {
        return _arr[index];
    }

    public void set(int index, T value) {
        _arr[index] = value;
    }

    public List<T> toList() {
        return new List<>(Arrays.stream(_arr).toList());
    }

    private class ArrayIterator implements Iterator<T> {
        int cursor;
        int lastRet = -1;

        ArrayIterator(int index) {
            cursor = index;
        }

        @Override
        public T next() {
            // TODO: Exceptions
            lastRet = cursor++;

            return _arr[lastRet];
        }

        @Override
        public boolean hasNext() {
            return cursor != _arr.length;
        }
    }
}
