package com.deblock.logger;

public class CGLogger {
    private static long startTurn = 0;
    private static boolean logEnabled = false;
    private static boolean submissionMode = false;

    public static void startTurn() {
        startTurn = System.currentTimeMillis();
    }

    public static void enableLog() {
        logEnabled = true;
    }
    public static void disableLog() {
        logEnabled = false;
    }

    public static void log(String message) {
        if (!submissionMode && logEnabled) {
            long duration = System.currentTimeMillis() - startTurn;
            System.err.println(duration + " : " + message);
        }
    }

    public static void submissionMode() {
        submissionMode = true;
    }
}
