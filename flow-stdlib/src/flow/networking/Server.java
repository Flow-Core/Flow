package flow.networking;

import flow.Function1;
import flow.Function2;
import flow.String;
import flow.Thing;
import flow.collections.ByteArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server<P extends Protocol> extends Thing {
    private int port;
    private Function2<P, OutputStream, ByteArray> encode;
    private Function1<InputStream, P> decode;

    private ExecutorService clientThreads;
    private ExecutorService listenThread;

    public Server<P> setup(int port, Function2<P, OutputStream, ByteArray> encode, Function1<InputStream, P> decode) {
        this.port = port;
        this.encode = encode;
        this.decode = decode;

        return this;
    }

    public Server<P> start() {
        clientThreads = Executors.newCachedThreadPool();

        listenThread = Executors.newSingleThreadExecutor();
        listenThread.submit(this::run);

        return this;
    }

    private void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                Socket<P> clientSocket = new Socket<>(server.accept(), encode, decode);
                clientThreads.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            close();
        }
    }

    private void handleClient(Socket<P> connection) {
        try {
            onConnect(connection);
        } catch (IOException ignored) {
        } finally {
            try {
                connection.close();
            } catch (IOException ignored) {
            } finally {
                onDisconnect(connection.id);
            }
        }
    }

    abstract void onConnect(Socket<P> connection) throws IOException;

    void onDisconnect(String connectionID) {}

    public void close() {
        listenThread.shutdown();
        clientThreads.shutdown();
    }
}