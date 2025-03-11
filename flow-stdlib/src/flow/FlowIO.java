package flow;

import java.util.Scanner;

public final class FlowIO {
    private static final Scanner scanner = new Scanner(System.in);

    private FlowIO() {}

    public static void println(String message) {
        System.out.println(message.value);
    }

    public static void println(Thing message) {
        System.out.println(message);
    }

    public static void print(String message) {
        System.out.print(message.value);
    }

    public static void print(Thing message) {
        System.out.println(message);
    }

    public static String input() {
        return new String(scanner.nextLine());
    }

    public static String input(Thing message) {
        System.out.println(message);
        return new String(scanner.nextLine());
    }
}
