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
            request.printTemplate("classes.txt", getClasses().length, getChic().getVMClassCount(), getChic().getVMUnloadedClassCount(), getChic().getVMTotalClassCount(), render(new TextTable()));
        } else {
            request.printTemplate("classes.html", getClasses().length, getChic().getVMClassCount(), getChic().getVMUnloadedClassCount(), getChic().getVMTotalClassCount(), render(new HtmlTable()));
        }
    }
}
