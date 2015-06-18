package com.onsite.chic;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Main class that processes class information and dumps it to
 * requests.
 *
 * @author Mike Virata-Stone
 */
public class Chic {
    private Instrumentation instrumentation;

    private static final String NO_PACKAGE = "[no_package]";

    private static final Comparator<Class> CLASS_NAME_COMPARATOR = new Comparator<Class>() {
        @Override
        public int compare(Class a, Class b) {
            return a.getName().compareTo(b.getName());
        }
    };

    public Chic(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
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
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf('.');

        if (lastDotIndex > 0) {
            return className.substring(0, lastDotIndex);
        }

        return NO_PACKAGE;
    }
}
