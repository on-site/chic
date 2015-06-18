package com.onsite.chic;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

/**
 * Main entry point into the Chic agent.
 *
 * @author Mike Virata-Stone
 */
public class Main {
    public static void main(String[] args) {
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws IOException {
        main(args, instrumentation);
    }

    public static void premain(String args, Instrumentation instrumentation) throws IOException {
        main(args, instrumentation);
    }

    private static void main(String args, Instrumentation instrumentation) throws IOException {
        new Server(new Chic(instrumentation)).start();
    }
}
