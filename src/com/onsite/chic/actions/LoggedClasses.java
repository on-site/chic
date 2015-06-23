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

    private String format(Date date) {
        return formatter.format(date);
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
        if (isTextRequest()) {
            request.printTemplate("logged_classes.txt", getClasses().size(), render(new TextTable()));
        } else {
            request.printTemplate("logged_classes.html", getClasses().size(), render(new HtmlTable()));
        }
    }
}
