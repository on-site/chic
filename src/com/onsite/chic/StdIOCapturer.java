package com.onsite.chic;

import java.io.Closeable;

/**
 * Capture native standard out.  Heavily inspired by
 * https://github.com/animetrics/STDIORedirect
 *
 * @author Mike Virata-Stone
 */
public class StdIOCapturer implements Closeable {
    private long pointer;

    public static StdIOCapturer forOut() {
        return new StdIOCapturer(1);
    }

    public static StdIOCapturer forErr() {
        return new StdIOCapturer(2);
    }

    private StdIOCapturer(int descriptor) {
        pointer = initNative(descriptor);
    }

    @Override
    public synchronized void close() {
        if (pointer == 0) {
            return;
        }

        destroyNative(pointer);
        pointer = 0;
    }

    public synchronized String read() {
        if (pointer == 0) {
            return null;
        }

        return read(pointer);
    }

    private native long initNative(int descriptor);
    private native void destroyNative(long pointer);
    private native String read(long pointer);
}
