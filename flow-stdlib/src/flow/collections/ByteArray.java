package flow.collections;

import flow.Byte;
import flow.Consumer2;
import flow.Int;

public class ByteArray implements Iterable<Byte> {
    byte[] _bytes;

    public ByteArray(int size) {
        _bytes = new byte[size];
    }

    @Override
    public Iterator<Byte> iterator() {
        return new ArrayIterator(0);
    }

    @Override
    public void forEachIndexed(Consumer2<Int, Byte> delegate) {
        ArrayIterator it = new ArrayIterator(0);

        while (it.hasNext()) {
            delegate.invoke(new Int(it.cursor), it.next());
        }
    }

    private class ArrayIterator implements Iterator<Byte> {
        int cursor;
        int lastRet = -1;

        ArrayIterator(int index) {
            cursor = index;
        }

        @Override
        public Byte next() {
            // TODO: Exceptions
            lastRet = cursor++;

            return Byte.fromPrimitive(_bytes[lastRet]);
        }

        @Override
        public boolean hasNext() {
            return cursor != _bytes.length;
        }
    }
}
