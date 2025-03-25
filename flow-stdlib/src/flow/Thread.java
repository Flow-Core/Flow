package flow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Thread extends Thing {
    private final ExecutorService thread;
    private boolean isAlive;

    public Thread(Procedure function) {
        thread = Executors.newSingleThreadExecutor(r -> {
            java.lang.Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        });

        isAlive = true;

        thread.execute(() -> {
            function.invoke();

            isAlive = false;
        });
    }

    public void kill() {
        thread.shutdownNow();
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive && !(thread.isShutdown() || thread.isTerminated());
    }

    public static void wait(float seconds) throws InterruptedException {
        if (seconds < 0) {
            // TODO: Exception
            return;
        }

        java.lang.Thread.sleep((long) (seconds * 1000));
    }

    public static Thread timeout(Procedure function, float seconds) {
        return new Thread(() -> {
            try {
                wait(seconds);
            } catch (InterruptedException e){
                return;
            }

            function.invoke();
        });
    }
}
