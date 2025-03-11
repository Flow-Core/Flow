package flow.networking;

import flow.Address;
import flow.Int;
import flow.Ip;

import java.io.IOException;

public class Socket {
    private java.net.Socket socket;

    public Socket(Ip ip, Int port) throws IOException {
        socket = new java.net.Socket(ip.value.value, port.value);
    }

    public Socket(Address address) throws IOException {
        socket = new java.net.Socket(address.ip.value.value, address.port.value);
    }
}
