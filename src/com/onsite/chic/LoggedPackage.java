package com.onsite.chic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoggedPackage {
    private String packageName;
    private List<LoggedClass> classes = new ArrayList<>();

    public LoggedPackage(String packageName) {
        this.packageName = packageName;
    }

    public synchronized Date getFirstLoggedAt() {
        if (classes.isEmpty()) {
            return null;
        }

        return classes.get(0).getLoggedAt();
    }

    public synchronized Date getLastLoggedAt() {
        if (classes.isEmpty()) {
            return null;
        }

        return classes.get(classes.size() - 1).getLoggedAt();
    }

    public String getPackageName() {
        return packageName;
    }

    public synchronized int size() {
        return classes.size();
    }

    public synchronized void add(LoggedClass log) {
        classes.add(log);
    }
}
