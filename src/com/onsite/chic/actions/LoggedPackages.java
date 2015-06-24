package com.onsite.chic.actions;

import com.onsite.chic.LoggedClass;
import com.onsite.chic.LoggedPackage;
import com.onsite.chic.Request;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Action to display all the packages that have been logged as loaded
 * via ClassLogger, along with general statistics.
 *
 * @author Mike Virata-Stone
 */
public class LoggedPackages extends Action {
    private List<LoggedPackage> packages;
    private List<LoggedClass> classes;
    private String firstLoggedAt;
    private String lastLoggedAt;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request, "/logged_packages", "/logged_packages.txt", "/logged_packages.csv");
    }

    private List<LoggedPackage> getPackages() {
        if (packages == null) {
            packages = getChic().getLoggedPackages();
        }

        return packages;
    }

    private List<LoggedClass> getClasses() {
        if (classes == null) {
            classes = getChic().getLoggedClasses();
        }

        return classes;
    }

    private String getFirstLoggedAt() {
        if (firstLoggedAt == null) {
            Date loggedAt = null;

            if (!getClasses().isEmpty()) {
                loggedAt = getClasses().get(0).getLoggedAt();
            }

            firstLoggedAt = format(loggedAt);
        }

        return firstLoggedAt;
    }

    private String getLastLoggedAt() {
        if (lastLoggedAt == null) {
            Date loggedAt = null;

            if (!getClasses().isEmpty()) {
                loggedAt = getClasses().get(getClasses().size() - 1).getLoggedAt();
            }

            lastLoggedAt = format(loggedAt);
        }

        return lastLoggedAt;
    }

    private String render(Table table) {
        table.header("Package Name", "Number of Classes", "First Logged At", "Last Logged At");

        for (LoggedPackage log : getPackages()) {
            String link = table.link(log.getPackageName(), "/logged_package/" + log.getPackageName());
            table.row(link, "" + log.size(), format(log.getFirstLoggedAt()), format(log.getLastLoggedAt()));
        }

        return table.render();
    }

    @Override
    public void process() throws IOException {
        if (isCsvRequest()) {
            request.printCsv(render(new CsvTable()));
        } else if (isTextRequest()) {
            request.printTemplate("logged_packages.txt", getPackages().size(), getClasses().size(), getFirstLoggedAt(), getLastLoggedAt(), render(new TextTable()));
        } else {
            request.printTemplate("logged_packages.html", getPackages().size(), getClasses().size(), getFirstLoggedAt(), getLastLoggedAt(), render(new HtmlTable()));
        }
    }
}
