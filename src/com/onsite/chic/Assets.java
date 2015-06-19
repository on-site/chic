package com.onsite.chic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * This class manages static assets contained in the assets resource
 * path.
 *
 * @author Mike Virata-Stone
 */
public class Assets {
    private Request request;

    public Assets(Request request) {
        this.request = request;
    }

    public boolean process() throws IOException {
        String path = request.getPath();

        if (!request.getVerb().equals("GET")) {
            return false;
        }

        if (!isValidAsset()) {
            return false;
        }

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        InputStream stream = getClass().getResourceAsStream("assets/" + path);

        if (stream == null) {
            return false;
        }

        try (BufferedReader input = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            request.printHeader(200, getMimeType());
            String line = input.readLine();

            while (line != null) {
                request.println(line);
                line = input.readLine();
            }
        }

        return true;
    }

    private boolean isValidAsset() {
        return request.getPath().endsWith(".js") || request.getPath().endsWith(".css");
    }

    private String getMimeType() {
        if (request.getPath().endsWith(".js")) {
            return "text/javascript";
        }

        if (request.getPath().endsWith(".css")) {
            return "text/css";
        }

        return "text/plain";
    }
}
