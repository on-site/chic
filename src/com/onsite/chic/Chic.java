package com.onsite.chic;

import java.lang.instrument.Instrumentation;

/**
 * Main class that processes class information and dumps it to
 * requests.
 *
 * @author Mike Virata-Stone
 */
public class Chic {
    private Instrumentation instrumentation;

    public Chic(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
}
