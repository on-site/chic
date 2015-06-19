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
        return isGet(request, "/packages");
    }

    private SortedMap<String, Integer> getPackageCounts() {
        if (packageCounts == null) {
            packageCounts = getChic().getPackageClassCounts();
        }

        return packageCounts;
    }

    private String getRowsHtml() {
        StringBuilder rows = new StringBuilder();

        for (String key : getPackageCounts().keySet()) {
            rows.append("<tr><td><a href=\"/package/");
            rows.append(key);
            rows.append("\">");
            rows.append(key);
            rows.append("</td><td>");
            rows.append(getPackageCounts().get(key));
            rows.append("</td></tr>\n");
        }

        return rows.toString();
    }

    @Override
    public void process() throws IOException {
        request.printTemplate("packages.html", getPackageCounts().size(), getRowsHtml());
    }
}
