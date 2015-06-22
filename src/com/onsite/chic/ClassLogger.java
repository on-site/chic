package com.onsite.chic;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Log classes that Java tells us have been loaded.  This uses native
 * code to process the verbose logging coming from Java.
 *
 * @author Mike Virata-Stone
 */
public class ClassLogger {
    private static final Pattern CLASS_LOG_PATTERN = Pattern.compile("^\\[Loaded (?<className>.*?) from (?<loadedFrom>.*?)\\]$");
    private List<LoggedClass> classes = new ArrayList<>();
    private volatile boolean stopped = false;
    private StdIOCapturer capturer;
    private PipedInputStream pipedInput;
    private PipedOutputStream pipedOutput;

    public synchronized void start() {
        if (capturer != null) {
            return;
        }

        setup();
        spawnReader();
        spawnProcessor();
    }

    private void setup() {
        try {
            capturer = StdIOCapturer.forOut();
            pipedInput = new PipedInputStream();
            pipedOutput = new PipedOutputStream(pipedInput);
        } catch (Exception e) {
            stop(e);
            closeCapturer();
            closePipedOutput();
            closePipedInput();
        }
    }

    private void spawnReader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!stopped) {
                        try {
                            String message = capturer.read();
                            pipedOutput.write(message.getBytes());
                        } catch (Exception e) {
                            stop(e);
                        }
                    }
                } finally {
                    closeCapturer();
                    closePipedOutput();
                }
            }
        });

        thread.setName("chic-class-logging-reader-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private void spawnProcessor() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(pipedInput))) {
                        while (!stopped) {
                            try {
                                processLine(reader.readLine());
                            } catch (Exception e) {
                                stop(e);
                            }
                        }
                    } catch (Exception e) {
                        stop(e);
                    }
                } finally {
                    closePipedInput();
                }
            }
        });

        thread.setName("chic-class-logging-processor-thread");
        thread.setDaemon(true);
        thread.start();
    }

    private void closeCapturer() {
        close("capturer", capturer);
    }

    private void closePipedInput() {
        close("piped input", pipedInput);
    }

    private void closePipedOutput() {
        if (pipedOutput == null) {
            return;
        }

        try {
            pipedOutput.flush();
        } catch (Exception e) {
            System.err.println("Chic: error while flushing last of piped output: " + e);
            e.printStackTrace();
        }

        close("piped output", pipedOutput);
    }

    private void close(String name, Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (Exception e) {
            System.err.println("Chic: error while closing " + name + ": " + e);
            e.printStackTrace();
        }
    }

    private void processLine(String line) {
        if (!line.startsWith("[Loaded ")) {
            return;
        }

        Matcher matcher = CLASS_LOG_PATTERN.matcher(line);

        if (!matcher.find()) {
            return;
        }

        String className = matcher.group("className");
        String loadedFrom = matcher.group("loadedFrom");
        classes.add(new LoggedClass(className, loadedFrom));
    }

    public List<LoggedClass> getClasses() {
        return new ArrayList<LoggedClass>(classes);
    }

    public void stop() {
        stopped = true;
        // Provide a little output just in case it is needed for the
        // reading to process it without trying to interrupt the
        // reading
        System.out.println("Stopping Chic class logging");
        System.out.flush();
    }

    private void stop(Exception e) {
        stop();
        System.err.println("Chic: class logging has been stopped due to an exception: " + e);
        e.printStackTrace();
    }
}
