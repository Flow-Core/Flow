package flow.networking.protocols;

import flow.String;
import flow.collections.ByteArray;
import flow.networking.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Raw extends Protocol {
    public int size;
    public ByteArray data;

    public Raw(int size, ByteArray data) {
        this.size = size;
        this.data = data;
    }

    public Raw(ByteArray data) {
        this.size = data.bytes.length;
        this.data = data;
    }

    public static ByteArray encode(Raw message, OutputStream out) throws IOException {
        out.write(message.data.bytes);

        return message.data;
    }

    public static Raw decode(InputStream in) throws IOException {
        ByteArray buffer = new ByteArray(in.readNBytes(4));

        int size = buffer.getInt();

        buffer = new ByteArray(in.readNBytes(size));

        return new Raw(size, buffer);
    }

    @Override
    public String string() {
        return data.getString();
    }
}
