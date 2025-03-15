package flow.networking;

import flow.*;
import flow.collections.ByteArray;

import java.io.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Socket<P extends Protocol> {
    private final java.net.Socket socket;
    private final InputStream sockIn;
    private final OutputStream sockOut;

    BiFunction<P, OutputStream, ByteArray> encode;
    Function<InputStream, P> decode;

    public Socket(Ip ip, Int port, BiFunction<P, OutputStream, ByteArray> encode, Function<InputStream, P> decode) throws IOException {
        socket = new java.net.Socket(ip.value.value, port.value);
        sockIn = socket.getInputStream();
        sockOut = socket.getOutputStream();

        this.encode = encode;
        this.decode = decode;
    }

    public Socket(Address address, BiFunction<P, OutputStream, ByteArray> encode, Function<InputStream, P> decode) throws IOException {
        socket = new java.net.Socket(address.ip.value.value, address.port.value);
        sockIn = socket.getInputStream();
        sockOut = socket.getOutputStream();

        this.encode = encode;
        this.decode = decode;
    }

    public void send(P message) {
        ByteArray bytes = encode.apply(message, sockOut);
    }

    public P receive() {
        return decode.apply(sockIn);
    }
}
