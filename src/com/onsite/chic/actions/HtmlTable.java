package com.onsite.chic.actions;

/**
 * An HTML table.
 *
 * @author Mike Virata-Stone
 */
public class HtmlTable extends Table {
    private StringBuilder table = new StringBuilder();
    private String rendered;

    public HtmlTable() {
        table.append("<table class=\"bordered\">\n<thead>\n");
    }

    @Override
    public void header(String... header) {
        table.append("<tr>");

        for (String th : header) {
            table.append("<th>");
            table.append(th);
            table.append("</th>");
        }

        table.append("</tr>\n</thead>\n<tbody>\n");
    }

    @Override
    public String link(String content, String href) {
        return "<a href=\"" + href + "\">" + content + "</a>";
    }

    @Override
    public void row(String... row) {
        table.append("<tr>");

        for (String cell : row) {
            table.append("<td>");
            table.append(cell);
            table.append("</td>");
        }

        table.append("</tr>\n");
    }

    @Override
    public String render() {
        if (rendered == null) {
            table.append("</tbody>\n</table>");
            rendered = table.toString();
        }

        return rendered;
    }
}
