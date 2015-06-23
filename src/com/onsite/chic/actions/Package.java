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

    private String render(Table table) {
        table.header("Class Name");

        for (Class clazz : getClasses()) {
            table.row(clazz.getName());
        }

        return table.render();
    }

    @Override
    public void process() throws IOException {
        if (isTextRequest()) {
            request.printTemplate("package.txt", getPackageName(), getClasses().length, render(new TextTable()));
        } else {
            request.printTemplate("package.html", getPackageName(), getClasses().length, render(new HtmlTable()));
        }
    }
}
