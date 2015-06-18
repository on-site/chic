package com.onsite.chic;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Main class that processes class information and dumps it to
 * requests.
 *
 * @author Mike Virata-Stone
 */
public class Chic {
    private Instrumentation instrumentation;

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

    public Class[] getSortedClasses() {
        Class[] classes = getClasses();
        Arrays.sort(classes, CLASS_NAME_COMPARATOR);
        return classes;
    }
}
