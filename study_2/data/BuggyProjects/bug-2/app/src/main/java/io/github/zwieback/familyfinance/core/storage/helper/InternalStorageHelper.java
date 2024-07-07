package io.github.zwieback.familyfinance.core.storage.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.function.Function;

import java.io.File;

public final class InternalStorageHelper {

    private static final String UPPER_LEVEL_DIR_NAME = "..";
    private static final String SHARED_PREFS_DIR_NAME = "shared_prefs";
    private static final String SAMSUNG_SHARED_PREFS_ROOT_PATH = "/dbdata/databases/";

    @NonNull
    public static File getInternalDbFile(@NonNull Context context, @NonNull String databaseName) {
        return context.getDatabasePath(databaseName);
    }

    @Nullable
    public static File getInternalSharedPrefsFile(@NonNull Context context,
                                                  @NonNull String sharedPrefsName) {
        File sharedPrefsFile = getInternalSharedPrefsFile(context, sharedPrefsName,
                InternalStorageHelper::buildRegularSharedPrefsDir);
        if (sharedPrefsFile != null) {
            return sharedPrefsFile;
        }
        return getInternalSharedPrefsFileFromSamsung(context, sharedPrefsName);
    }

    private InternalStorageHelper() {
    }

    /**
     * Workaround to get the file of shared preferences from Samsung devices.
     *
     * @see <a href="https://stackoverflow.com/a/5533412/8035065">
     * Copy the shared preferences XML file from /data on Samsung device failed
     * </a>
     */
    @Nullable
    private static File getInternalSharedPrefsFileFromSamsung(@NonNull Context context,
                                                              @NonNull String sharedPrefsName) {
        return getInternalSharedPrefsFile(context, sharedPrefsName,
                InternalStorageHelper::buildSamsungSharedPrefsDir);
    }

    @Nullable
    private static File
    getInternalSharedPrefsFile(@NonNull Context context,
                               @NonNull String sharedPrefsName,
                               @NonNull Function<Context, File> sharedPrefsDirBuildFunction) {
        File sharedPrefsDir = sharedPrefsDirBuildFunction.apply(context);
        File sharedPrefsFile = new File(sharedPrefsDir, sharedPrefsName);
        if (sharedPrefsFile.exists()) {
            return sharedPrefsFile;
        }
        return null;
    }

    @NonNull
    private static File buildRegularSharedPrefsDir(@NonNull Context context) {
        return new File(context.getFilesDir(), UPPER_LEVEL_DIR_NAME + File.separator +
                SHARED_PREFS_DIR_NAME);
    }

    @NonNull
    private static File buildSamsungSharedPrefsDir(@NonNull Context context) {
        String packageName = context.getPackageName();
        return new File(SAMSUNG_SHARED_PREFS_ROOT_PATH + packageName + File.separator +
                SHARED_PREFS_DIR_NAME);
    }
}
