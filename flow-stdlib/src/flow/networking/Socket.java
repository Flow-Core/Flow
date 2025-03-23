package flow.networking;

import flow.*;
import flow.String;
import flow.collections.ByteArray;

import java.io.*;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public class Socket<P extends Protocol> {
    private final java.net.Socket socket;
    private final InputStream sockIn;
    private final OutputStream sockOut;

    Function2<P, OutputStream, ByteArray> encode;
    Function1<InputStream, P> decode;

    public final String id = new String(UUID.randomUUID().toString());

    public Socket(Ip ip, Int port, Function2<P, OutputStream, ByteArray> encode, Function1<InputStream, P> decode) throws IOException {
        socket = new java.net.Socket(ip.value.value, port.value);
        sockIn = socket.getInputStream();
        sockOut = socket.getOutputStream();

        this.encode = encode;
        this.decode = decode;
    }

    public Socket(Address address, Function2<P, OutputStream, ByteArray> encode, Function1<InputStream, P> decode) throws IOException {
        socket = new java.net.Socket(address.ip.value.value, address.port.value);
        sockIn = socket.getInputStream();
        sockOut = socket.getOutputStream();

        this.encode = encode;
        this.decode = decode;
    }

    public Socket(java.net.Socket socket, Function2<P, OutputStream, ByteArray> encode, Function1<InputStream, P> decode) throws IOException {
        this.socket = socket;
        sockIn = socket.getInputStream();
        sockOut = socket.getOutputStream();

        this.encode = encode;
        this.decode = decode;
    }

    public void send(P message) {
        ByteArray bytes = encode.invoke(message, sockOut);
    }

    public P receive() {
        return decode.invoke(sockIn);
    }

    public void close() throws IOException {
        socket.close();
    }
}
