package flow;

public class Address extends Thing {
    public final Ip ip;
    public final Int port;

    public Address(Ip ip, Int port){
        this.ip = ip;
        this.port = port;
    }

    // Overriding string() to return a java.lang.String representation
    @Override
    public String string() {
        return new String(ip.value + ":" + port);
    }

    // Creates a copy of this Int wrapper
    @Override
    public Address copy() {
        return new Address(ip, port);
    }
}
