package com.onsite.chic;

import java.io.IOException;
import java.util.SortedMap;

public class Actions {
    private Request request;

    public Actions(Request request) {
        this.request = request;
    }

    public boolean process() throws IOException {
        if (request.getVerb().equals("GET")) {
            switch (request.getPath()) {
            case "/":
                index();
                return true;
            case "/classes":
                classes();
                return true;
            case "/packages":
                packages();
                return true;
            }

            if (request.getPath().startsWith("/package/")) {
                singlePackage(request.getPath().substring("/package/".length()));
                return true;
            }
        } else {
            switch (request.getPath()) {
            case "/shutdown":
                shutdown();
                return true;
            }
        }

        return false;
    }

    private void index() throws IOException {
        request.printTemplate("index.html");
    }

    private void classes() throws IOException {
        Class[] classes = request.getServer().getChic().getSortedClasses();
        StringBuilder rows = new StringBuilder();

        for (Class clazz : classes) {
            rows.append("<tr><td>");
            rows.append(clazz.getName());
            rows.append("</td></tr>\n");
        }

        request.printTemplate("classes.html", classes.length, rows.toString());
    }

    private void packages() throws IOException {
        SortedMap<String, Integer> packageCounts = request.getServer().getChic().getPackageClassCounts();
        StringBuilder rows = new StringBuilder();

        for (String key : packageCounts.keySet()) {
            rows.append("<tr><td><a href=\"/package/");
            rows.append(key);
            rows.append("\">");
            rows.append(key);
            rows.append("</td><td>");
            rows.append(packageCounts.get(key));
            rows.append("</td></tr>\n");
        }

        request.printTemplate("packages.html", packageCounts.size(), rows.toString());
    }

    private void singlePackage(String packageName) throws IOException {
        Class[] classes = request.getServer().getChic().getSortedClasses(packageName);
        StringBuilder rows = new StringBuilder();

        for (Class clazz : classes) {
            rows.append("<tr><td>");
            rows.append(clazz.getName());
            rows.append("</td></tr>\n");
        }

        request.printTemplate("package.html", packageName, classes.length, rows.toString());
    }

    private void shutdown() throws IOException {
        request.getServer().shutdown();
        request.printText("The server is now shut down.");
    }
}
