package com.onsite.chic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Main entry point into the Chic agent.
 *
 * @author Mike Virata-Stone
 */
public class Main {
    public static void main(String[] args) throws MalformedURLException, ReflectiveOperationException, URISyntaxException {
        if (args.length < 1) {
            System.err.println("usage: java chic.jar <pid> [agent_options]");
            System.exit(1);
        }

        String pid = args[0];
        String agentOptions = "";

        if (args.length > 1) {
            agentOptions = args[1];
        }

        attach(pid, agentOptions);
    }

    private static void attach(String pid, String agentOptions) throws MalformedURLException, ReflectiveOperationException, URISyntaxException {
        URLClassLoader classLoader = new URLClassLoader(new URL[] { getToolsJarFile().toURI().toURL() }, Main.class.getClassLoader());
        Class<?> VirtualMachine = Class.forName("com.sun.tools.attach.VirtualMachine", true, classLoader);
        Method attach = VirtualMachine.getMethod("attach", String.class);
        Method loadAgent = VirtualMachine.getMethod("loadAgent", String.class, String.class);
        Method detach = VirtualMachine.getDeclaredMethod("detach");
        Object vm = attach.invoke(null, pid);

        try {
            if (!agentOptions.equals("")) {
                agentOptions += ",";
            }

            String jarFilePath = getJarFile().getAbsolutePath();
            agentOptions += "jar=" + jarFilePath;
            loadAgent.invoke(vm, jarFilePath, agentOptions);
        } finally {
            detach.invoke(vm);
        }
    }

    private static File getToolsJarFile() {
        return new File(new File(System.getProperty("java.home")), "../lib/tools.jar");
    }

    private static File getJarFile(String args) throws URISyntaxException {
        if (args != null && !args.trim().equals("")) {
            for (String arg : args.split(",")) {
                String[] parts = arg.split("=", 2);
                String key = parts[0];
                String value = null;

                if (parts.length > 1) {
                    value = parts[1];
                }

                if (key.equals("jar") && value != null) {
                    return new File(value);
                }
            }
        }

        return getJarFile();
    }

    private static File getJarFile() throws URISyntaxException {
        ProtectionDomain domain = Main.class.getProtectionDomain();
        CodeSource source = domain.getCodeSource();
        return new File(source.getLocation().toURI());
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws IOException, MalformedURLException, ReflectiveOperationException, URISyntaxException {
        startChic(args, instrumentation);
    }

    public static void premain(String args, Instrumentation instrumentation) throws IOException, MalformedURLException, ReflectiveOperationException, URISyntaxException {
        startChic(args, instrumentation);
    }

    // Start in a separate classloader so a new version can be loaded without conflict
    private static void startChic(String args, Instrumentation instrumentation) throws IOException, MalformedURLException, ReflectiveOperationException, URISyntaxException {
        CachedClassLoader classLoader = new CachedClassLoader(getJarFile(args), Main.class.getClassLoader());
        Class<?> Chic = Class.forName("com.onsite.chic.Chic", true, classLoader);
        Constructor<?> constructor = Chic.getConstructor(Instrumentation.class, String.class);
        Method start = Chic.getMethod("start");
        Object chic = constructor.newInstance(instrumentation, args);
        start.invoke(chic);
    }
}
