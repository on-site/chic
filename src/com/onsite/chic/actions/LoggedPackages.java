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

    private String format(Date date) {
        if (date == null) {
            return "-";
        }

        return formatter.format(date);
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
            request.printTemplate("logged_packages.txt", getPackages().size(), render(new TextTable()));
        } else {
            request.printTemplate("logged_packages.html", getPackages().size(), render(new HtmlTable()));
        }
    }
}
