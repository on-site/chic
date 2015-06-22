package com.onsite.chic;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Capture native standard out.  Heavily inspired by
 * https://github.com/animetrics/STDIORedirect
 *
 * @author Mike Virata-Stone
 */
public class StdIOCapturer implements Closeable {
    private long pointer;

    static {
        try {
            File nativeFile = File.createTempFile("chic.native.StdIOCapturer", ".so");
            nativeFile.deleteOnExit();
            byte[] buffer = new byte[1024];

            try (FileOutputStream output = new FileOutputStream(nativeFile);
                    InputStream input = StdIOCapturer.class.getResourceAsStream("StdIOCapturer.so")) {
                int size = input.read(buffer);

                while (size >= 0) {
                    output.write(buffer, 0, size);
                    size = input.read(buffer);
                }
            }

            System.load(nativeFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error preparing native library", e);
        }
    }

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
