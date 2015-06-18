package com.onsite.chic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * Handle a single HTTP request and respond with the requested data.
 *
 * @author Mike Virata-Stone
 */
public class Request {
    private static final Pattern SPLITTER = Pattern.compile(" ");

    private Chic chic;
    private Socket socket;
    private PrintWriter writer;

    public Request(Chic chic, Socket socket) {
        this.chic = chic;
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

                String verb = parts[0];
                String path = parts[1];

                if (!verb.equals("GET")) {
                    invalidVerb(verb);
                    return;
                }

                switch (path) {
                case "/":
                    index();
                    break;
                default:
                    invalidPath(path);
                }
            }
        } finally {
            socket.close();
        }
    }

    private void print(String message) {
        writer.print(message);
    }

    private void println(String message) {
        writer.println(message);
    }

    private void printHeader(int status, String mimeType) {
        print("HTTP/1.0 " + status + " OK\r\nContent-Type: " + mimeType + ";charset=UTF-8\r\n\r\n");
    }

    private void printHeader() {
        printHeader(200, "text/html");
    }

    private void printError(int status, String message) {
        printHeader(status, "text/plain");
        print(message);
    }

    private void invalidRequest(String request) {
        printError(400, "Invalid request: " + request);
    }

    private void invalidVerb(String verb) {
        printError(501, "Only GET requests are allowed, got: " + verb);
    }

    private void invalidPath(String path) {
        printError(404, "Invalid path: " + path);
    }

    private void index() {
        printHeader();
        println("<html>");
        println("  <body>");
        println("    Welcome to Chic!  More to come.");
        println("  </body>");
        println("</html>");
    }
}
