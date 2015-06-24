package com.onsite.chic;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Main class that processes class information and dumps it to
 * requests.
 *
 * @author Mike Virata-Stone
 */
public class Chic {
    private static final String NO_PACKAGE = "[no_package]";

    private Instrumentation instrumentation;
    private Map<String, String> args;
    private Boolean wasClassLoggingOn;
    private ClassLogger classLogger = new ClassLogger();

    private static final Comparator<Class> CLASS_NAME_COMPARATOR = new Comparator<Class>() {
        @Override
        public int compare(Class a, Class b) {
            return a.getName().compareTo(b.getName());
        }
    };

    public Chic(Instrumentation instrumentation, String args) {
        this.instrumentation = instrumentation;
        setArgs(args);
    }

    private void setArgs(String args) {
        this.args = new HashMap<>();

        if (args == null || args.trim().equals("")) {
            return;
        }

        for (String arg : args.split(",")) {
            String[] parts = arg.split("=", 2);
            String key = parts[0];
            String value = null;

            if (parts.length > 1) {
                value = parts[1];
            }

            this.args.put(key, value);
        }
    }

    public void start() throws IOException {
        Server server = new Server(this);

        if (args.get("port") != null) {
            server.setPort(Integer.parseInt(args.get("port")));
        }

        if (args.get("threads") != null) {
            server.setMaxThreads(Integer.parseInt(args.get("threads")));
        }

        if (args.get("bind") != null) {
            server.setBindAddress(args.get("bind"));
        }

        server.start();
    }

    private ClassLoadingMXBean getClassLoadingBean() {
        return ManagementFactory.getClassLoadingMXBean();
    }

    public int getVMClassCount() {
        return getClassLoadingBean().getLoadedClassCount();
    }

    public long getVMUnloadedClassCount() {
        return getClassLoadingBean().getUnloadedClassCount();
    }

    public long getVMTotalClassCount() {
        return getClassLoadingBean().getTotalLoadedClassCount();
    }

    public void startClassLogging() {
        if (!"true".equals(args.get("logclassloading"))) {
            return;
        }

        if (wasClassLoggingOn == null) {
            wasClassLoggingOn = getClassLoadingBean().isVerbose();
        }

        classLogger.start();
        getClassLoadingBean().setVerbose(true);
    }

    public void stopClassLogging() {
        if (!"true".equals(args.get("logclassloading"))) {
            return;
        }

        if (wasClassLoggingOn != null) {
            getClassLoadingBean().setVerbose(wasClassLoggingOn);
        }

        classLogger.stop();
    }

    public List<LoggedClass> getLoggedClasses() {
        return classLogger.getClasses();
    }

    public List<LoggedPackage> getLoggedPackages() {
        return classLogger.getPackages();
    }

    public LoggedPackage getLoggedPackage(String packageName) {
        return classLogger.getPackage(packageName);
    }

    public Class[] getClasses() {
        return instrumentation.getAllLoadedClasses();
    }

    public SortedMap<String, Integer> getPackageClassCounts() {
        SortedMap<String, Integer> results = new TreeMap<>();

        for (Class clazz : getClasses()) {
            String packageName = getPackageName(clazz);

            if (results.containsKey(packageName)) {
                results.put(packageName, results.get(packageName) + 1);
            } else {
                results.put(packageName, 1);
            }
        }

        return results;
    }

    public Class[] getSortedClasses() {
        Class[] classes = getClasses();
        Arrays.sort(classes, CLASS_NAME_COMPARATOR);
        return classes;
    }

    public Class[] getSortedClasses(String packageName) {
        List<Class> classes = new ArrayList<>();

        for (Class clazz : getClasses()) {
            if (getPackageName(clazz).equals(packageName)) {
                classes.add(clazz);
            }
        }

        Collections.sort(classes, CLASS_NAME_COMPARATOR);
        return classes.toArray(new Class[0]);
    }

    private static String getPackageName(Class clazz) {
        return getPackageName(clazz.getName());
    }

    public static String getPackageName(String className) {
        int lastDotIndex = className.lastIndexOf('.');

        if (lastDotIndex > 0) {
            return className.substring(0, lastDotIndex);
        }

        return NO_PACKAGE;
    }
}
