package com.onsite.chic.actions;

import com.onsite.chic.Request;

import java.io.IOException;

/**
 * Index action for the root landing page.
 *
 * @author Mike Virata-Stone
 */
public class Index extends Action {
    @Override
    public boolean canProcess(Request request) {
        return isGet(request, "/");
    }

    @Override
    public void process() throws IOException {
        request.printTemplate("index.html");
    }
}
