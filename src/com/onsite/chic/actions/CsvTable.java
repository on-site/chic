package com.onsite.chic.actions;

/**
 * A CSV table.
 *
 * @author Mike Virata-Stone
 */
public class CsvTable extends Table {
    private StringBuilder table = new StringBuilder();

    @Override
    public void header(String... header) {
        row(header);
    }

    @Override
    public void row(String... row) {
        for (int i = 0; i < row.length; i++) {
            if (i != 0) {
                table.append(',');
            }

            table.append(row[i]);
        }

        table.append('\n');
    }

    @Override
    public String render() {
        return table.toString();
    }
}
