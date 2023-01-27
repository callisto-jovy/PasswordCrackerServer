package net.bplaced.abzzezz.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ZipUtil {


    public static final String ZIP_PATH = "crack.zip";


    public static String convertZipToBase64() {
        try {
            final byte[] bytes = Files.readAllBytes(new File(ZIP_PATH).toPath());

            return Base64.getEncoder().encodeToString(bytes);
        } catch (final IOException e) {
            e.printStackTrace();
            return "null";
        }
    }


}
