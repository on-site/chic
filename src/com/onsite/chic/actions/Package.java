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
        }

        return packageName;
    }

    private Class[] getClasses() {
        if (classes == null) {
            classes = getChic().getSortedClasses(getPackageName());
        }

        return classes;
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
        request.printTemplate("package.html", getPackageName(), getClasses().length, getRowsHtml());
    }
}
