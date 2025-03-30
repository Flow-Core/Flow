package flow;

import java.util.concurrent.ThreadLocalRandom;

public class Random {
    public static int nextInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static int nextInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static float next() {
        return ThreadLocalRandom.current().nextFloat(1);
    }
}
