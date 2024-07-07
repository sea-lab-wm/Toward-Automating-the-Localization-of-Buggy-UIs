package io.github.zwieback.familyfinance.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * Creates the specified {@code destinationFile} as a byte for byte copy of the
     * {@code sourceFile}. If {@code destinationFile} already exists, then it
     * will be replaced with a copy of {@code sourceFile}. The name and path
     * of {@code destinationFile} will be that of {@code destinationFile}.
     * <p>
     * <i>Note: {@code sourceFile} and {@code destinationFile} will be closed by
     * this method.</i>
     *
     * @param sourceFile      file to copy from
     * @param destinationFile file to copy to
     */
    public static void copyFile(File sourceFile, File destinationFile) throws IOException {
        Log.d(TAG, "copyFile: " + destinationFile.getPath() + " is exists: " +
                destinationFile.exists());
        Log.d(TAG, "copyFile: " + destinationFile.getParentFile().getPath() + ".mkdirs: " +
                destinationFile.getParentFile().mkdirs());

        try (InputStream fis = new FileInputStream(sourceFile);
             OutputStream output = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        }
    }

    private FileUtils() {
    }
}
