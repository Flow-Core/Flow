package flow.collections;

import flow.*;
import flow.Byte;
import flow.String;

import java.util.Arrays;

public class ByteArray extends Thing implements Iterable<Byte> {
    public byte[] bytes;

    public ByteArray(int size) {
        bytes = new byte[size];
    }

    public ByteArray(byte[] bytes) {
        this.bytes = bytes.clone();
    }

    public short getShort() {
        return (short) ((bytes[0] << 0x08) |
                        (bytes[1]));
    }

    public int getInt() {
        return (bytes[0] << 0x18) |
               (bytes[1] << 0x10) |
               (bytes[2] << 0x08) |
               (bytes[3]);
    }

    public long getLong() {
        return ((long) bytes[0] << 0x38) |
               ((long) bytes[1] << 0x30) |
               ((long) bytes[2] << 0x28) |
               ((long) bytes[3] << 0x20) |
               (bytes[4] << 0x18) |
               (bytes[5] << 0x10) |
               (bytes[6] << 0x08) |
               (bytes[7]);
    }

    public String getString() {
        return new String(new java.lang.String(bytes));
    }

    @Override
    public java.lang.String toString() {
        return Arrays.toString(bytes);
    }

    @Override
    public String string() {
        return new String(Arrays.toString(bytes));
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

            return Byte.fromPrimitive(bytes[lastRet]);
        }

        @Override
        public boolean hasNext() {
            return cursor != bytes.length;
        }
    }
}
