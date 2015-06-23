package com.onsite.chic;

import java.util.Date;

/**
 * Data class representing when and from where a class was loaded.
 *
 * @author Mike Virata-Stone
 */
public class LoggedClass {
    private Date loggedAt;
    private String className;
    private String loadedFrom;
    private String packageName;

    public LoggedClass(String className, String loadedFrom) {
        this.loggedAt = new Date();
        this.className = className;
        this.loadedFrom = loadedFrom;
        this.packageName = Chic.getPackageName(className);
    }

    public Date getLoggedAt() {
        return loggedAt;
    }

    public String getClassName() {
        return className;
    }

    public String getLoadedFrom() {
        return loadedFrom;
    }

    public String getPackageName() {
        return packageName;
    }
}
