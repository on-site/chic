package com.onsite.chic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.regex.Pattern;

/**
 * Handle a single HTTP request and respond with the requested data.
 *
 * @author Mike Virata-Stone
 */
public class Request {
    private static final Pattern SPLITTER = Pattern.compile(" ");

    private Server server;
    private Socket socket;
    private String verb;
    private String path;
    private PrintWriter writer;

    public Request(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void process() throws IOException {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))) {
                this.writer = writer;
                String line = reader.readLine();

                if (line == null) {
                    return;
                }

                String[] parts = SPLITTER.split(line, 3);

                if (parts.length < 2) {
                    invalidRequest(line);
                    return;
                }

                verb = parts[0];
                path = parts[1];

                if (!server.getRouter().route(this)) {
                    invalidPath(path);
                }
            }
        } finally {
            socket.close();
        }
    }

    public Server getServer() {
        return server;
    }

    public String getVerb() {
        return verb;
    }

    public String getPath() {
        return path;
    }

    public void print(String message) {
        writer.print(message);
    }

    public void println(String message) {
        writer.println(message);
    }

    public void printHeader(int status, String mimeType) {
        print("HTTP/1.0 " + status + " OK\r\nContent-Type: " + mimeType + ";charset=UTF-8\r\n\r\n");
    }

    private void printError(int status, String message) {
        printHeader(status, "text/plain");
        print(message);
    }

    private void invalidRequest(String request) {
        printError(400, "Invalid request: " + request);
    }

    private void invalidPath(String path) {
        printError(404, "Invalid path: " + path);
    }

    public void printText(String message) {
        printHeader(200, "text/plain");
        print(message);
    }

    public void printCsv(String csv) {
        printHeader(200, "text/csv");
        print(csv);
    }

    public void printTemplate(String file, Object... args) throws IOException {
        StringBuilder view = new StringBuilder();
        InputStream stream = getClass().getResourceAsStream("views/" + file);

        // When the jar changes, this seems to happen sometimes
        if (stream == null) {
            throw new FileNotFoundException("Cannot find template " + file);
        }

        try (BufferedReader input = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line = input.readLine();

            while (line != null) {
                view.append(line);
                view.append("\n");
                line = input.readLine();
            }
        }

        printHeader(200, getMimeType(file, "text/html"));
        print(MessageFormat.format(view.toString(), args));
    }

    public static String getMimeType(String path) {
        return getMimeType(path, "text/plain");
    }

    public static String getMimeType(String path, String ifUnknown) {
        int lastDotIndex = path.lastIndexOf('.');

        if (lastDotIndex < 0) {
            return ifUnknown;
        }

        switch (path.substring(lastDotIndex)) {
        case ".js":
            return "text/javascript";
        case ".css":
            return "text/css";
        case ".html":
            return "text/html";
        case ".txt":
            return "text/plain";
        case ".csv":
            return "text/csv";
        }

        return ifUnknown;
    }
}
