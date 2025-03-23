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
    private ServerSocket server;

    private int port;
    private Function2<P, OutputStream, ByteArray> encode;
    private Function1<InputStream, P> decode;

    private ExecutorService clientThreads;
    private ExecutorService listenThread;

    private boolean isServerRunning = true;

    public Server<P> setup(int port, Function2<P, OutputStream, ByteArray> encode, Function1<InputStream, P> decode) {
        this.port = port;
        this.encode = encode;
        this.decode = decode;

        return this;
    }

    public Server<P> start() {
        clientThreads = Executors.newCachedThreadPool();

        listenThread = Executors.newSingleThreadExecutor();

        listenThread.execute(this::run);

        return this;
    }

    private void run() {
        try {
            server = new ServerSocket(port);
            onStart();

            while (isServerRunning) {
                Socket<P> clientSocket = new Socket<>(server.accept(), encode, decode);
                clientThreads.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();

            try {
                close();
            } catch (IOException ignore) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket<P> connection) {
        try {
            onConnect(connection);
        } finally {
            try {
                connection.close();
            } catch (IOException ignored) {} finally {
                onDisconnect(connection.id);
            }
        }
    }

    public void onStart() {}

    public abstract void onConnect(Socket<P> connection);

    public void onDisconnect(String connectionID) {}

    public void close() throws IOException {
        isServerRunning = false;

        server.close();

        listenThread.shutdownNow();
        clientThreads.shutdownNow();
    }
}