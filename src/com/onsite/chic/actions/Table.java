package com.onsite.chic.actions;

/**
 * A table of content that can be rendered in a number of different
 * styles using the subclasses.
 *
 * @author Mike Virata-Stone
 */
public abstract class Table {
    public abstract void header(String... header);
    public abstract void row(String... row);
    public abstract String render();

    public String link(String content, String href) {
        return content;
    }
}
