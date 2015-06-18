package com.onsite.chic;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.Comparator;
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
            String className = clazz.getName();
            String packageName = NO_PACKAGE;
            int lastDotIndex = className.lastIndexOf('.');

            if (lastDotIndex > 0) {
                packageName = className.substring(0, lastDotIndex);
            }

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
}
