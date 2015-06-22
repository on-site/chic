package com.onsite.chic.actions;

import com.onsite.chic.LoggedClass;
import com.onsite.chic.Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Action to display all the classes that have been logged as loaded
 * via ClassLogger, along with general statistics.
 *
 * @author Mike Virata-Stone
 */
public class LoggedClasses extends Action {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private List<LoggedClass> classes;
    private Integer classNameWidth;
    private Integer loadLocationWidth;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request, "/logged_classes", "/logged_classes.txt");
    }

    private List<LoggedClass> getClasses() {
        if (classes == null) {
            classes = getChic().getLoggedClasses();
        }

        return classes;
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
            table.append(log.getClassName());
            table.append("| ");
            table.append(log.getLoadedFrom());
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
            request.printTemplate("logged_classes.txt", getClasses().size(), getTextTable());
        } else {
            request.printTemplate("logged_classes.html", getClasses().size(), getRowsHtml());
        }
    }
}
