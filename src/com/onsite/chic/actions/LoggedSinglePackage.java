package com.onsite.chic.actions;

import com.onsite.chic.LoggedClass;
import com.onsite.chic.LoggedPackage;
import com.onsite.chic.Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Action to display the details of a logged package... how many
 * classes, which classes, etc.
 *
 * @author Mike Virata-Stone
 */
public class LoggedSinglePackage extends Action {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private String packageName;
    private List<LoggedClass> classes;
    private String firstLoggedAt;
    private String lastLoggedAt;
    private Integer classNameWidth;
    private Integer loadLocationWidth;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request) && request.getPath().startsWith("/logged_package/");
    }

    private String getPackageName() {
        if (packageName == null) {
            packageName = request.getPath().substring("/logged_package/".length());

            if (packageName.endsWith(".txt")) {
                packageName = packageName.substring(0, packageName.length() - ".txt".length());
            }
        }

        return packageName;
    }

    private List<LoggedClass> getClasses() {
        if (classes == null) {
            classes = getChic().getLoggedPackage(getPackageName()).getClasses();
        }

        return classes;
    }

    private String getFirstLoggedAt() {
        if (firstLoggedAt == null) {
            firstLoggedAt = format(getChic().getLoggedPackage(getPackageName()).getFirstLoggedAt());
        }

        return firstLoggedAt;
    }

    private String getLastLoggedAt() {
        if (lastLoggedAt == null) {
            lastLoggedAt = format(getChic().getLoggedPackage(getPackageName()).getLastLoggedAt());
        }

        return lastLoggedAt;
    }

    private int getClassNameWidth() {
        if (classNameWidth == null) {
            loadWidths();
        }

        return classNameWidth;
    }

    private int getLoadLocationWidth() {
        if (loadLocationWidth == null) {
            loadWidths();
        }

        return loadLocationWidth;
    }

    private void loadWidths() {
        classNameWidth = "Class Name".length();
        loadLocationWidth = "Loaded From".length();

        for (LoggedClass log : getClasses()) {
            if (log.getClassName().length() > classNameWidth) {
                classNameWidth = log.getClassName().length();
            }

            if (log.getLoadedFrom().length() > loadLocationWidth) {
                loadLocationWidth = log.getLoadedFrom().length();
            }
        }

        classNameWidth++;
        loadLocationWidth++;
    }

    private String format(Date date) {
        if (date == null) {
            return "-";
        }

        return formatter.format(date);
    }

    private String getTextTable() {
        StringBuilder table = new StringBuilder();
        table.append(spacePad("Class Name", getClassNameWidth()));
        table.append("| ");
        table.append(spacePad("Loaded From", getLoadLocationWidth()));
        table.append("| Loaded At\n");
        table.append(dashes(getClassNameWidth()));
        table.append("+-");
        table.append(dashes(getLoadLocationWidth()));
        table.append("+----------\n");

        for (LoggedClass log : getClasses()) {
            table.append(spacePad(log.getClassName(), getClassNameWidth()));
            table.append("| ");
            table.append(spacePad(log.getLoadedFrom(), getLoadLocationWidth()));
            table.append("| ");
            table.append(format(log.getLoggedAt()));
            table.append("\n");
        }

        return table.toString();
    }

    private String getRowsHtml() {
        StringBuilder rows = new StringBuilder();

        for (LoggedClass log : getClasses()) {
            rows.append("<tr><td>");
            rows.append(log.getClassName());
            rows.append("</td><td>");
            rows.append(log.getLoadedFrom());
            rows.append("</td><td>");
            rows.append(format(log.getLoggedAt()));
            rows.append("</td></tr>\n");
        }

        return rows.toString();
    }

    @Override
    public void process() throws IOException {
        if (isTextRequest()) {
            request.printTemplate("logged_package.txt", getPackageName(), getClasses().size(), getFirstLoggedAt(), getLastLoggedAt(), getTextTable());
        } else {
            request.printTemplate("logged_package.html", getPackageName(), getClasses().size(), getFirstLoggedAt(), getLastLoggedAt(), getRowsHtml());
        }
    }
}
