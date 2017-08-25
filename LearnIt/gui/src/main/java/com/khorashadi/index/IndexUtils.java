package com.khorashadi.index;

import org.jsoup.Jsoup;

import java.io.File;

public class IndexUtils {
    private static final String INDEX_DIR_NAME = "index";
    public static String extractText(String html) {
        return Jsoup.parse(html).text();
    }

    public static String getIndexDirectoryFromBase(String baseDirectory) {
        return new File(baseDirectory, INDEX_DIR_NAME).getAbsolutePath();
    }
}
