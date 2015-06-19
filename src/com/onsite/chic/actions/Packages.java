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
    private Integer packageNameWidth;

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

    private int getPackageNameWidth() {
        if (packageNameWidth == null) {
            packageNameWidth = "Package Name".length();

            for (String key : getPackageCounts().keySet()) {
                if (key.length() > packageNameWidth) {
                    packageNameWidth = key.length();
                }
            }

            packageNameWidth++;
        }

        return packageNameWidth;
    }

    private String getTextTable() {
        StringBuilder table = new StringBuilder();
        table.append(spacePad("Package Name", getPackageNameWidth()));
        table.append("| Number of Classes\n");
        table.append(dashes(getPackageNameWidth()));
        table.append("+------------------\n");

        for (String key : getPackageCounts().keySet()) {
            table.append(spacePad(key, getPackageNameWidth()));
            table.append("| ");
            table.append(getPackageCounts().get(key));
            table.append("\n");
        }

        return table.toString();
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
        if (isTextRequest()) {
            request.printTemplate("packages.txt", getPackageCounts().size(), getTextTable());
        } else {
            request.printTemplate("packages.html", getPackageCounts().size(), getRowsHtml());
        }
    }
}
