package io.github.zwieback.familyfinance.core.storage.helper;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import io.github.zwieback.familyfinance.core.storage.exception.UncheckedIOException;
import io.github.zwieback.familyfinance.util.StringUtils;

public final class ExternalStorageHelper {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    @Nullable
    public static File getExternalDbFile(@NonNull Context context,
                                         @NonNull String databaseName,
                                         @Nullable String backupPath) {
        return getExternalFile(context, databaseName, backupPath);
    }

    @Nullable
    public static File getExternalSharedPrefsFile(@NonNull Context context,
                                                  @NonNull String sharedPrefsName,
                                                  @Nullable String backupPath) {
        return getExternalFile(context, sharedPrefsName, backupPath);
    }

    private ExternalStorageHelper() {
    }

    @Nullable
    private static File getExternalFile(@NonNull Context context,
                                        @NonNull String sharedPrefsName,
                                        @Nullable String backupPath) {
        if (StringUtils.isTextNotEmpty(backupPath)) {
            return getNullableExternalFile(backupPath, sharedPrefsName);
        }
        return getDefaultExternalFile(context, sharedPrefsName);
    }

    @Nullable
    private static File getDefaultExternalFile(@NonNull Context context, @NonNull String fileName) {
        File[] externalDirs = ContextCompat.getExternalFilesDirs(context, null);
        File externalDir = externalDirs.length > 0 ? externalDirs[0] : null;
        if (externalDir != null) {
            return getNullableExternalFile(externalDir.getAbsolutePath(), fileName);
        }
        return null;
    }

    @Nullable
    private static File getNullableExternalFile(@NonNull String backupPath,
                                                @NonNull String fileName) {
        File externalFile = new File(backupPath, fileName);
        if (externalFile.exists()) {
            return externalFile;
        }
        try {
            if (externalFile.createNewFile()) {
                return externalFile;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
        return null;
    }
}
