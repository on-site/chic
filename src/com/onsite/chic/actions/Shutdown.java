package com.onsite.chic.actions;

import com.onsite.chic.Request;

import java.io.IOException;

/**
 * Action to clean up as many resources as possible that Chic uses.
 * This may not be perfect, but it will shut down the server, thread
 * pool, and as much stored data as can be released.
 *
 * @author Mike Virata-Stone
 */
public class Shutdown extends Action {
    @Override
    public boolean canProcess(Request request) {
        return isPost(request, "/shutdown");
    }

    @Override
    public void process() throws IOException {
        request.getServer().shutdown();
        request.printText("The server is now shut down.");
    }
}
