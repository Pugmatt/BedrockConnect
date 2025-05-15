package main.com.pyratron.pugmatt.bedrockconnect.logging;

public class LogColors {
    public static final String RESET = "\u001B[0m";

    private static boolean disabled = Boolean.getBoolean("stripColors");

    public static String blue(String msg) {
        return apply(msg, "\u001B[34m");
    }

    public static String cyan(String msg) {
        return apply(msg, "\u001B[36m");
    }

    public static String purple(String msg) {
        return apply(msg, "\u001B[35m");
    }

    public static String red(String msg) {
        return apply(msg, "\u001B[31m");
    }
    
    public static String green(String msg) {
        return apply(msg, "\u001B[32m");
    }

    public static String yellow(String msg) {
        return apply(msg, "\u001B[33m");
    }

    public static String gray(String msg) {
        return apply(msg, "\u001B[38;5;240m");
    }

    private static String apply(String msg, String color) {
        if (disabled) return msg;

        return color  + msg + LogColors.RESET;
    }
}