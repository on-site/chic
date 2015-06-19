package com.onsite.chic.actions;

import com.onsite.chic.Chic;
import com.onsite.chic.Request;
import com.onsite.chic.Server;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Base class for any action in the view.
 *
 * @author Mike Virata-Stone
 */
public abstract class Action {
    private Constructor<? extends Action> constructor;
    protected Request request;

    public Action() {
        try {
            constructor = getClass().getConstructor();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to initialize action", e);
        }
    }

    protected boolean isGet(Request request, String... paths) {
        return isPath("GET", request, paths);
    }

    protected boolean isPost(Request request, String... paths) {
        return isPath("POST", request, paths);
    }

    private boolean isPath(String verb, Request request, String... paths) {
        if (!verb.equals(request.getVerb())) {
            return false;
        }

        if (paths.length == 0) {
            return true;
        }

        return isPath(request, paths);
    }

    protected boolean isPath(Request request, String... paths) {
        for (String path : paths) {
            if (path.equals(request.getPath())) {
                return true;
            }
        }

        return false;
    }

    protected Server getServer() {
        return request.getServer();
    }

    protected Chic getChic() {
        return getServer().getChic();
    }

    public abstract boolean canProcess(Request request);
    public abstract void process() throws IOException;

    public final void process(Request request) throws IOException {
        try {
            Action action = constructor.newInstance();
            action.request = request;
            action.process();
        } catch (ReflectiveOperationException e) {
            throw new IOException("Failed to initialize action", e);
        }
    }
}
