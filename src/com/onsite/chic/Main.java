package com.onsite.chic;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
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
            loadAgent.invoke(vm, getJarFile().getAbsolutePath(), agentOptions);
        } finally {
            detach.invoke(vm);
        }
    }

    private static File getToolsJarFile() {
        return new File(new File(System.getProperty("java.home")), "../lib/tools.jar");
    }

    private static File getJarFile() throws URISyntaxException {
        ProtectionDomain domain = Main.class.getProtectionDomain();
        CodeSource source = domain.getCodeSource();
        return new File(source.getLocation().toURI());
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws IOException {
        main(args, instrumentation);
    }

    public static void premain(String args, Instrumentation instrumentation) throws IOException {
        main(args, instrumentation);
    }

    private static void main(String args, Instrumentation instrumentation) throws IOException {
        new Chic(instrumentation, args).start();
    }
}
