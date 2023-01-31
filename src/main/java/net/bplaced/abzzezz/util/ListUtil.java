package net.bplaced.abzzezz.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class ListUtil {


    public static final String PASSWORD_LIST_URL = "https://github.com/brannondorsey/naive-hashcat/releases/download/data/rockyou.txt";



    public static InputStream createInputStream() throws IOException {
        return new URL(PASSWORD_LIST_URL).openStream();
    }

    /**
     * @Deprecated
     *
     */
    public static String convertZipToBase64() {
        try {
            final byte[] bytes = Files.readAllBytes(new File("crack.zip").toPath());

            return Base64.getEncoder().encodeToString(bytes);
        } catch (final IOException e) {
            e.printStackTrace();
            return "null";
        }
    }


}
