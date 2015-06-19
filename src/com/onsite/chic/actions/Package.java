package com.onsite.chic.actions;

import com.onsite.chic.Request;

import java.io.IOException;

/**
 * Action to view the details of a single package... how many classes
 * it has loaded, and which ones.
 *
 * @author Mike Virata-Stone
 */
public class Package extends Action {
    private String packageName;
    private Class[] classes;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request) && request.getPath().startsWith("/package/");
    }

    private String getPackageName() {
        if (packageName == null) {
            packageName = request.getPath().substring("/package/".length());

            if (packageName.endsWith(".txt")) {
                packageName = packageName.substring(0, packageName.length() - ".txt".length());
            }
        }

        return packageName;
    }

    private Class[] getClasses() {
        if (classes == null) {
            classes = getChic().getSortedClasses(getPackageName());
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
            request.printTemplate("package.txt", getPackageName(), getClasses().length, getTextTable());
        } else {
            request.printTemplate("package.html", getPackageName(), getClasses().length, getRowsHtml());
        }
    }
}
