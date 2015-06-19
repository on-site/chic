package com.onsite.chic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class loader that reads all the files in the provided jar ahead of
 * time, then loads classes from the already read entries.  Resources
 * will be returned from the cache if retrieved as a stream, however
 * resource URLs will use the default ClassLoader behavior.
 *
 * @author Mike Virata-Stone
 */
public class CachedClassLoader extends ClassLoader {
    private Map<String, byte[]> data;

    public CachedClassLoader(File jarFile, ClassLoader parent) throws IOException {
        super(parent);
        readJar(jarFile);
    }

    private void readJar(File jarFile) throws IOException {
        data = new HashMap<>();
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            try (InputStream input = jar.getInputStream(entry)) {
                data.put(entry.getName(), readBytes(input));
            }
        }
    }

    private byte[] readBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int data = input.read();

        while (data != -1) {
            buffer.write(data);
            data = input.read();
        }

        return buffer.toByteArray();
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        if (data.containsKey(name)) {
            return new ByteArrayInputStream(data.get(name));
        }

        return super.getResourceAsStream(name);
    }

    @Override
    public Class loadClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/') + ".class";

        if (data.containsKey(path)) {
            return defineClass(name, data.get(path), 0, data.get(path).length);
        }

        return super.loadClass(name);
    }
}
