package com.afsu.mod.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DebugLogger {
    private static volatile boolean active = false;
    private static final java.util.concurrent.LinkedBlockingQueue<String> logQueue = new java.util.concurrent.LinkedBlockingQueue<>();
    
    private static final String START_CMD = "CMD:START";
    private static final String STOP_CMD = "CMD:STOP";
    private static final String POISON_PILL = "CMD:POISON";
    
    private static final Thread loggingThread;

    static {
        loggingThread = new Thread(() -> {
            PrintWriter writer = null;
            while (true) {
                try {
                    String msg = logQueue.take();
                    if (msg.equals(POISON_PILL)) {
                        if (writer != null) {
                            writer.println("=== END PROFILING SESSION ===");
                            writer.close();
                        }
                        break;
                    } else if (msg.equals(START_CMD)) {
                        if (writer == null) {
                            try {
                                File dir = new File("crash-reports/logs");
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                File file = new File(dir, "afsu_tracker.log");
                                writer = new PrintWriter(new FileWriter(file, true));
                                writer.println("=== NEW PROFILING SESSION ===");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (msg.equals(STOP_CMD)) {
                        if (writer != null) {
                            writer.println("=== END PROFILING SESSION ===");
                            writer.close();
                            writer = null;
                        }
                    } else {
                        if (writer != null) {
                            writer.println(msg);
                            writer.flush();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "AFSU-DebugLogger-Thread");
        loggingThread.setDaemon(true);
        loggingThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logQueue.offer(POISON_PILL);
            try {
                loggingThread.join(2000);
            } catch (InterruptedException e) {
            }
        }));
    }

    public static synchronized boolean toggleLogging() {
        if (active) {
            stopLogging();
        } else {
            startLogging();
        }
        return active;
    }

    public static synchronized void startLogging() {
        if (active) return;
        active = true;
        logQueue.offer(START_CMD);
    }

    public static synchronized void stopLogging() {
        if (!active) return;
        active = false;
        logQueue.offer(STOP_CMD);
    }

    public static void log(String message) {
        if (active) {
            logQueue.offer(message);
        }
    }

    public static boolean isLogging() {
        return active;
    }
}
