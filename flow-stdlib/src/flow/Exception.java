package flow;

public class Exception extends java.lang.Exception {
    public Exception() {}

    public Exception(String message) {
        super(message.value);
    }

    public Exception(java.lang.String message) {
        super(message);
    }
}