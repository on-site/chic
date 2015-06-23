package com.onsite.chic.actions;

import com.onsite.chic.LoggedPackage;
import com.onsite.chic.Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Action to display all the packages that have been logged as loaded
 * via ClassLogger, along with general statistics.
 *
 * @author Mike Virata-Stone
 */
public class LoggedPackages extends Action {
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private List<LoggedPackage> packages;
    private Integer packageNameWidth;
    private Integer numClassesWidth;
    private Integer firstLoggedWidth;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request, "/logged_packages", "/logged_packages.txt");
    }

    private List<LoggedPackage> getPackages() {
        if (packages == null) {
            packages = getChic().getLoggedPackages();
        }

        return packages;
    }

    private int getPackageNameWidth() {
        if (packageNameWidth == null) {
            loadWidths();
        }

        return packageNameWidth;
    }

    private int getNumClassesWidth() {
        if (numClassesWidth == null) {
            loadWidths();
        }

        return numClassesWidth;
    }

    private int getFirstLoggedWidth() {
        if (firstLoggedWidth == null) {
            loadWidths();
        }

        return firstLoggedWidth;
    }

    private void loadWidths() {
        packageNameWidth = "Package Name".length();
        numClassesWidth = "Number of Classes".length();
        firstLoggedWidth = "First Logged At".length();

        // Num classes width is super unlikely to be bigger than the
        // header and the time is constant for each row, so ignore the
        // former and handle the latter here
        String firstFirstLoggedAt = format(getPackages().get(0).getFirstLoggedAt());

        if (!getPackages().isEmpty() && firstFirstLoggedAt.length() > firstLoggedWidth) {
            firstLoggedWidth = firstFirstLoggedAt.length();
        }

        for (LoggedPackage log : getPackages()) {
            if (log.getPackageName().length() > packageNameWidth) {
                packageNameWidth = log.getPackageName().length();
            }
        }

        packageNameWidth++;
        numClassesWidth++;
        firstLoggedWidth++;
    }

    private String format(Date date) {
        if (date == null) {
            return "-";
        }

        return formatter.format(date);
    }

    private String getTextTable() {
        StringBuilder table = new StringBuilder();
        table.append(spacePad("Package Name", getPackageNameWidth()));
        table.append("| ");
        table.append(spacePad("Number of Classes", getNumClassesWidth()));
        table.append("| ");
        table.append(spacePad("First Logged At", getFirstLoggedWidth()));
        table.append("| Last Logged At\n");
        table.append(dashes(getPackageNameWidth()));
        table.append("+-");
        table.append(dashes(getNumClassesWidth()));
        table.append("+-");
        table.append(dashes(getFirstLoggedWidth()));
        table.append("+---------------\n");

        for (LoggedPackage log : getPackages()) {
            table.append(spacePad(log.getPackageName(), getPackageNameWidth()));
            table.append("| ");
            table.append(spacePad("" + log.size(), getNumClassesWidth()));
            table.append("| ");
            table.append(spacePad(format(log.getFirstLoggedAt()), getFirstLoggedWidth()));
            table.append("| ");
            table.append(format(log.getLastLoggedAt()));
            table.append("\n");
        }

        return table.toString();
    }

    private String getRowsHtml() {
        StringBuilder rows = new StringBuilder();

        for (LoggedPackage log : getPackages()) {
            rows.append("<tr><td><a href=\"/logged_package/");
            rows.append(log.getPackageName());
            rows.append("\">");
            rows.append(log.getPackageName());
            rows.append("</a></td><td>");
            rows.append(log.size());
            rows.append("</td><td>");
            rows.append(format(log.getFirstLoggedAt()));
            rows.append("</td><td>");
            rows.append(format(log.getLastLoggedAt()));
            rows.append("</td></tr>\n");
        }

        return rows.toString();
    }

    @Override
    public void process() throws IOException {
        if (isTextRequest()) {
            request.printTemplate("logged_packages.txt", getPackages().size(), getTextTable());
        } else {
            request.printTemplate("logged_packages.html", getPackages().size(), getRowsHtml());
        }
    }
}
