package flow.networking.protocols;

import flow.String;
import flow.collections.ByteArray;
import flow.networking.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Text extends Protocol {
    public String data;
    public static String delimiter = new String("\n");

    public Text(String data) {
        this.data = data;
    }

    public static ByteArray encode(Text message, OutputStream out) throws IOException {
        out.write(message.data.plus(delimiter).toByteArray().bytes);

        return message.data.toByteArray();
    }

    public static Text decode(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();

        int ch;

        while ((ch = in.read()) != -1) {
            sb.append((char) ch);
            if (
                sb.length() >= delimiter.length() &&
                sb.substring(sb.length() - delimiter.length()).equals(delimiter.value)
            ) {
                String message = new String(sb.substring(0, sb.length() - delimiter.length()));
                return new Text(message);
            }
        }

        return null;
    }

    @Override
    public String string() {
        return data;
    }
}
