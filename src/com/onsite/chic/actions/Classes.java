package com.onsite.chic.actions;

import com.onsite.chic.Request;

import java.io.IOException;

/**
 * Action to display all the classes that have been loaded, along with
 * general statistics.
 *
 * @author Mike Virata-Stone
 */
public class Classes extends Action {
    private Class[] classes;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request, "/classes", "/classes.txt");
    }

    private Class[] getClasses() {
        if (classes == null) {
            classes = getChic().getSortedClasses();
        }

        return classes;
    }

    private String getTextTable() {
        StringBuilder table = new StringBuilder();
        table.append("Class Name\n");
        table.append("----------\n");

        for (Class clazz : getClasses()) {
            table.append(clazz.getName());
            table.append("\n");
        }

        return table.toString();
    }

    private String getRowsHtml() {
        StringBuilder rows = new StringBuilder();

        for (Class clazz : getClasses()) {
            rows.append("<tr><td>");
            rows.append(clazz.getName());
            rows.append("</td></tr>\n");
        }

        return rows.toString();
    }

    @Override
    public void process() throws IOException {
        if (isTextRequest()) {
            request.printTemplate("classes.txt", getClasses().length, getChic().getVMClassCount(), getChic().getVMUnloadedClassCount(), getChic().getVMTotalClassCount(), getTextTable());
        } else {
            request.printTemplate("classes.html", getClasses().length, getChic().getVMClassCount(), getChic().getVMUnloadedClassCount(), getChic().getVMTotalClassCount(), getRowsHtml());
        }
    }
}
