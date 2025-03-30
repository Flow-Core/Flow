package flow.collections;

import flow.Consumer2;
import flow.Int;
import flow.Thing;

import java.util.Arrays;

public class Array<T> extends Thing implements Iterable<T> {
    T[] _arr;

    @SuppressWarnings("unchecked")
    public Array(int size) {
        _arr = (T[]) new Object[size];
    }

    public Array(T[] arr) {
        _arr = arr.clone();
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

    public static <T> Array<T> fromPrimitiveArray(T[] primArr) {
        Array<T> result = new Array<>(primArr.length);

        for (int i = 0; i < primArr.length; i++) {
            result.set(i, primArr[i]);
        }

        return result;
    }

    public static <T> T[] toPrimitiveArray(Array<T> arr, Class<T> componentType) {
        T[] primArr = (T[]) java.lang.reflect.Array.newInstance(componentType, arr._arr.length);
        for (int i = 0; i < arr._arr.length; i++) {
            primArr[i] = arr.get(i);
        }
        return primArr;
    }

    @Override
    public String toString() {
        return Arrays.toString(_arr);
    }

    @Override
    public flow.String string() {
        return new flow.String(Arrays.toString(_arr));
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
