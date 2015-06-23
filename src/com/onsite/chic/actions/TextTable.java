package com.onsite.chic.actions;

import java.util.ArrayList;
import java.util.List;

/**
 * A text table.
 *
 * @author Mike Virata-Stone
 */
public class TextTable extends Table {
    private String rendered;
    private String[] header;
    private int[] sizes;
    private List<String[]> rows = new ArrayList<>();
    private StringBuilder table = new StringBuilder();

    @Override
    public void header(String... header) {
        this.header = header;
    }

    @Override
    public void row(String... row) {
        rows.add(row);
    }

    @Override
    public String render() {
        if (rendered == null) {
            renderHeader();
            renderTable();
            rendered = table.toString();
        }

        return rendered;
    }

    private void renderHeader() {
        renderRow(header);

        for (int i = 0; i < header.length; i++) {
            if (i > 0) {
                table.append("+-");
            }

            appendDashes(getSize(i));
        }

        table.append('\n');
    }

    private void renderRow(String[] row) {
        for (int i = 0; i < row.length; i++) {
            if (i > 0) {
                table.append("| ");
            }

            table.append(row[i]);

            if (i < row.length - 1) {
                appendSpaces(row[i], getSize(i));
            }
        }

        table.append('\n');
    }

    private void appendSpaces(String value, int size) {
        for (int _ = size - value.length(); _ > 0; _--) {
            table.append(' ');
        }
    }

    protected void appendDashes(int size) {
        for (int _ = size; _ > 0; _--) {
            table.append('-');
        }
    }

    private void renderTable() {
        for (String[] row : rows) {
            renderRow(row);
        }
    }

    private int getSize(int index) {
        return getSizes()[index];
    }

    private int[] getSizes() {
        if (sizes == null) {
            sizes = new int[header.length];

            for (int i = 0; i < sizes.length; i++) {
                sizes[i] = header[i].length();
            }

            for (String[] row : rows) {
                for (int i = 0; i < sizes.length; i++) {
                    if (row[i].length() > sizes[i]) {
                        sizes[i] = row[i].length();
                    }
                }
            }

            for (int i = 0; i < sizes.length; i++) {
                sizes[i]++;
            }
        }

        return sizes;
    }
}
