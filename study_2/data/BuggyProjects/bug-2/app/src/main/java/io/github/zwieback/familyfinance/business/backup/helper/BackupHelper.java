package io.github.zwieback.familyfinance.business.backup.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import io.github.zwieback.familyfinance.core.model.Models;
import io.github.zwieback.familyfinance.util.FileUtils;

import static io.github.zwieback.familyfinance.core.storage.helper.ExternalStorageHelper.getExternalDbFile;
import static io.github.zwieback.familyfinance.core.storage.helper.ExternalStorageHelper.getExternalSharedPrefsFile;
import static io.github.zwieback.familyfinance.core.storage.helper.InternalStorageHelper.getInternalDbFile;
import static io.github.zwieback.familyfinance.core.storage.helper.InternalStorageHelper.getInternalSharedPrefsFile;

public final class BackupHelper {

    public static boolean backupDatabase(@NonNull Context context,
                                         @Nullable String backupPath) throws IOException {
        File internalDb = getInternalDbFile(context, getDatabaseName());
        File externalDb = getExternalDbFile(context, getDatabaseName(), backupPath);
        if (externalDb == null) {
            return false;
        }
        FileUtils.copyFile(internalDb, externalDb);
        return externalDb.exists();
    }

    public static boolean backupSharedPreferences(@NonNull Context context,
                                                  @NonNull String sharedPrefsName,
                                                  @Nullable String backupPath) throws IOException {
        File internalFile = getInternalSharedPrefsFile(context, sharedPrefsName);
        File externalFile = getExternalSharedPrefsFile(context, sharedPrefsName, backupPath);
        if (internalFile == null || externalFile == null) {
            return false;
        }
        FileUtils.copyFile(internalFile, externalFile);
        return externalFile.exists();
    }

    public static boolean restoreDatabase(@NonNull Context context,
                                          @Nullable String backupPath) throws IOException {
        File internalDb = getInternalDbFile(context, getDatabaseName());
        File externalDb = getExternalDbFile(context, getDatabaseName(), backupPath);
        if (externalDb == null) {
            return false;
        }
        FileUtils.copyFile(externalDb, internalDb);
        return internalDb.exists();
    }

    public static boolean restoreSharedPreferences(@NonNull Context context,
                                                   @NonNull String sharedPrefsName,
                                                   @Nullable String backupPath) throws IOException {
        File internalFile = getInternalSharedPrefsFile(context, sharedPrefsName);
        File externalFile = getExternalSharedPrefsFile(context, sharedPrefsName, backupPath);
        if (internalFile == null || externalFile == null) {
            return false;
        }
        FileUtils.copyFile(externalFile, internalFile);
        return internalFile.exists();
    }

    private BackupHelper() {
    }

    private static String getDatabaseName() {
        return Models.DEFAULT.getName();
    }
}
