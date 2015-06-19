package com.onsite.chic.actions;

import com.onsite.chic.Request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Action to render a static asset like JS and CSS files.
 *
 * @author Mike Virata-Stone
 */
public class Asset extends Action {
    @Override
    public boolean canProcess(Request request) {
        if (!isGet(request)) {
            return false;
        }

        if (!isValidAsset(request)) {
            return false;
        }

        // Because of how the cached class loader works, we cannot use
        // getResource, only getResourceAsStream
        try {
            try (InputStream stream = getStream(request)) {
                return stream != null;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private static InputStream getStream(Request request) {
        String path = request.getPath();

        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return request.getClass().getResourceAsStream("assets/" + path);
    }

    private static boolean isValidAsset(Request request) {
        return request.getPath().endsWith(".js") || request.getPath().endsWith(".css");
    }

    @Override
    public void process() throws IOException {
        request.printHeader(200, Request.getMimeType(request.getPath()));

        try (BufferedReader input = new BufferedReader(new InputStreamReader(getStream(request), "UTF-8"))) {
            String line = input.readLine();

            while (line != null) {
                request.println(line);
                line = input.readLine();
            }
        }
    }
}
