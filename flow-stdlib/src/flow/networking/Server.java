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
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server<P extends Protocol> extends Thing {
    public int port;
    public ConcurrentHashMap<String, Socket<P>> connections = new ConcurrentHashMap<>();

    private Function2<P, OutputStream, ByteArray> encode;
    private Function1<InputStream, P> decode;
    private ServerSocket server;

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
        clientThreads = Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });

        listenThread = Executors.newSingleThreadExecutor(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        listenThread.execute(this::run);

        return this;
    }

    private void run() {
        try {
            server = new ServerSocket(port);
            onStart();

            while (isServerRunning) {
                Socket<P> clientSocket = new Socket<>(server.accept(), encode, decode);
                connections.put(clientSocket.id, clientSocket);
                clientThreads.execute(() -> handleClient(clientSocket));
            }
        } catch (SocketException e) {
            if (isServerRunning) {
                e.printStackTrace();
            }

            try {
                close();
            } catch (IOException ignore) {
                e.printStackTrace();
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
            connections.remove(connection.id);
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

        for (Socket<P> client : connections.values()) {
            client.close();
        }

        if (!server.isClosed()) {
            server.close();
        }

        listenThread.shutdownNow();
        clientThreads.shutdownNow();
    }
}