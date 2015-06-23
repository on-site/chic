package com.onsite.chic.actions;

import com.onsite.chic.Request;

import java.io.IOException;
import java.util.SortedMap;

/**
 * Action for viewing all the packages that have been loaded, along
 * with the counts for each.
 *
 * @author Mike Virata-Stone
 */
public class Packages extends Action {
    private SortedMap<String, Integer> packageCounts;

    @Override
    public boolean canProcess(Request request) {
        return isGet(request, "/packages", "/packages.txt");
    }

    private SortedMap<String, Integer> getPackageCounts() {
        if (packageCounts == null) {
            packageCounts = getChic().getPackageClassCounts();
        }

        return packageCounts;
    }

    private String render(Table table) {
        table.header("Package Name", "Number of Classes");

        for (String key : getPackageCounts().keySet()) {
            String link = table.link(key, "/package/" + key);
            table.row(link, "" + getPackageCounts().get(key));
        }

        return table.render();
    }

    @Override
    public void process() throws IOException {
        if (isTextRequest()) {
            request.printTemplate("packages.txt", getPackageCounts().size(), render(new TextTable()));
        } else {
            request.printTemplate("packages.html", getPackageCounts().size(), render(new HtmlTable()));
        }
    }
}
