package com.onsite.chic;

import java.io.IOException;
import java.net.Socket;

/**
 * Handle a single HTTP request and respond with the requested data.
 *
 * @author Mike Virata-Stone
 */
public class Request {
    private Chic chic;
    private Socket socket;

    public Request(Chic chic, Socket socket) {
        this.chic = chic;
        this.socket = socket;
    }

    public void process() throws IOException {
        try {
            // TODO
        } finally {
            socket.close();
        }
    }
}
