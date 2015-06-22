package com.onsite.chic;

import com.onsite.chic.actions.Action;
import com.onsite.chic.actions.Asset;
import com.onsite.chic.actions.Classes;
import com.onsite.chic.actions.Index;
import com.onsite.chic.actions.LoggedClasses;
import com.onsite.chic.actions.Package;
import com.onsite.chic.actions.Packages;
import com.onsite.chic.actions.Shutdown;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Routes an incoming request to the proper action.
 *
 * @author Mike Virata-Stone
 */
public class Router {
    private List<Action> routes = new ArrayList<Action>();

    public Router() {
        routes.add(new Index());
        routes.add(new Classes());
        routes.add(new LoggedClasses());
        routes.add(new Packages());
        routes.add(new Package());
        routes.add(new Shutdown());
        routes.add(new Asset());
    }

    public boolean route(Request request) throws IOException {
        for (Action action : routes) {
            if (action.canProcess(request)) {
                action.process(request);
                return true;
            }
        }

        return false;
    }
}
