package com.onsite.chic.actions;

import com.onsite.chic.LoggedClass;
import com.onsite.chic.LoggedPackage;
import com.onsite.chic.Request;

import java.io.IOException;
import java.util.List;

/**
 * Action to display the details of a logged package... how many
 * classes, which classes, etc.
 *
 * @author Mike Virata-Stone
 */
public class LoggedSinglePackage extends Action {
    private String packageName;
    private List<LoggedClass> classes;
    private String firstLoggedAt;
    private String lastLoggedAt;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request) && request.getPath().startsWith("/logged_package/");
    }

    private String getPackageName() {
        if (packageName == null) {
            packageName = request.getPath().substring("/logged_package/".length());

            if (packageName.endsWith(".txt")) {
                packageName = packageName.substring(0, packageName.length() - ".txt".length());
            } else if (packageName.endsWith(".csv")) {
                packageName = packageName.substring(0, packageName.length() - ".csv".length());
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

    private String render(Table table) {
        table.header("Class Name", "Loaded From", "Loaded At");

        for (LoggedClass log : getClasses()) {
            table.row(log.getClassName(), log.getLoadedFrom(), format(log.getLoggedAt()));
        }

        return table.render();
    }

    @Override
    public void process() throws IOException {
        if (isCsvRequest()) {
            request.printCsv(render(new CsvTable()));
        } else if (isTextRequest()) {
            request.printTemplate("logged_package.txt", getPackageName(), getClasses().size(), getFirstLoggedAt(), getLastLoggedAt(), render(new TextTable()));
        } else {
            request.printTemplate("logged_package.html", getPackageName(), getClasses().size(), getFirstLoggedAt(), getLastLoggedAt(), render(new HtmlTable()));
        }
    }
}
