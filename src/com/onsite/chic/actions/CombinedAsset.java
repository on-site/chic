package com.onsite.chic.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class CombinedAsset extends Asset {
    @Override
    protected String getAssetPath(String path) {
        return super.getAssetPath(path) + ".manifest";
    }

    @Override
    protected void printAsset() throws IOException {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(getStream(request), "UTF-8"))) {
            String line = input.readLine();

            while (line != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                printAsset(getStream(line));
                line = input.readLine();
            }
        }
    }
}
