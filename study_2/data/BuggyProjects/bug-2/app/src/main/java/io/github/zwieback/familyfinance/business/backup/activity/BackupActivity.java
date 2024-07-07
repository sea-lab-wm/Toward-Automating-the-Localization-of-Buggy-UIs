package io.github.zwieback.familyfinance.business.backup.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.backup.helper.BackupHelper;
import io.github.zwieback.familyfinance.core.activity.ActivityWrapper;
import io.github.zwieback.familyfinance.core.preference.config.BackupPrefs;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;
import io.github.zwieback.familyfinance.core.storage.helper.ExternalStorageHelper;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class BackupActivity extends ActivityWrapper {

    private static final String XML_EXTENSION = ".xml";

    private BackupPrefs backupPrefs;
    private DatabasePrefs databasePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void setupContentView() {
        setContentView(R.layout.activity_backup);
    }

    @Override
    protected int getTitleStringId() {
        return R.string.backup_activity_title;
    }

    private void init() {
        backupPrefs = BackupPrefs.with(this);
        databasePrefs = DatabasePrefs.with(this);
        findViewById(R.id.backup_database_button).setOnClickListener(v ->
                BackupActivityPermissionsDispatcher
                        .onBackupDatabaseClickWithPermissionCheck(this, v));
        findViewById(R.id.restore_database_button).setOnClickListener(v ->
                BackupActivityPermissionsDispatcher
                        .onRestoreDatabaseClickWithPermissionCheck(this, v));
        findViewById(R.id.backup_shared_prefs_button).setOnClickListener(v ->
                BackupActivityPermissionsDispatcher
                        .onBackupSharedPrefsClickWithPermissionCheck(this, v));
        findViewById(R.id.restore_shared_prefs_button).setOnClickListener(v ->
                BackupActivityPermissionsDispatcher
                        .onRestoreSharedPrefsClickWithPermissionCheck(this, v));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BackupActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onBackupDatabaseClick(View view) {
        if (!ExternalStorageHelper.isExternalStorageWritable()) {
            Toast.makeText(this, R.string.write_external_storage_permission_is_missing,
                    Toast.LENGTH_LONG).show();
            return;
        }
        try {
            boolean backupCompletedSuccessfully = BackupHelper.backupDatabase(this,
                    backupPrefs.getBackupPath());
            int resultMessage = getBackupResultMessage(backupCompletedSuccessfully);
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onRestoreDatabaseClick(View view) {
        if (!ExternalStorageHelper.isExternalStorageReadable()) {
            Toast.makeText(this, R.string.read_external_storage_permission_is_missing,
                    Toast.LENGTH_LONG).show();
            return;
        }
        try {
            boolean restoreCompletedSuccessfully = BackupHelper.restoreDatabase(this,
                    backupPrefs.getBackupPath());
            int resultMessage = getRestoreResultMessage(restoreCompletedSuccessfully);
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onBackupSharedPrefsClick(View view) {
        if (!ExternalStorageHelper.isExternalStorageWritable()) {
            Toast.makeText(this, R.string.write_external_storage_permission_is_missing,
                    Toast.LENGTH_LONG).show();
            return;
        }
        try {
            boolean backupCompletedSuccessfully = BackupHelper.backupSharedPreferences(this,
                    getBackupSharedPreferencesName(), backupPrefs.getBackupPath());
            backupCompletedSuccessfully |= BackupHelper.backupSharedPreferences(this,
                    getDatabaseSharedPreferencesName(), backupPrefs.getBackupPath());
            backupCompletedSuccessfully |= BackupHelper.backupSharedPreferences(this,
                    getDefaultSharedPreferencesName(this), backupPrefs.getBackupPath());
            int resultMessage = getBackupResultMessage(backupCompletedSuccessfully);
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void onRestoreSharedPrefsClick(View view) {
        if (!ExternalStorageHelper.isExternalStorageReadable()) {
            Toast.makeText(this, R.string.read_external_storage_permission_is_missing,
                    Toast.LENGTH_LONG).show();
            return;
        }
        try {
            boolean restoreCompletedSuccessfully = BackupHelper.restoreSharedPreferences(this,
                    getBackupSharedPreferencesName(), backupPrefs.getBackupPath());
            restoreCompletedSuccessfully |= BackupHelper.restoreSharedPreferences(this,
                    getDatabaseSharedPreferencesName(), backupPrefs.getBackupPath());
            restoreCompletedSuccessfully |= BackupHelper.restoreSharedPreferences(this,
                    getDefaultSharedPreferencesName(this), backupPrefs.getBackupPath());
            // todo refresh shared preferences after a successful restore
            int resultMessage = getRestoreResultMessage(restoreCompletedSuccessfully);
            Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForWriteExternalStorage(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_write_external_storage_rationale)
                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForWriteExternalStorage() {
        Toast.makeText(this, R.string.permission_write_external_storage_denied,
                Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showNeverAskForWriteExternalStorage() {
        Toast.makeText(this, R.string.permission_write_external_storage_never_ask,
                Toast.LENGTH_LONG).show();
    }

    @StringRes
    private int getBackupResultMessage(boolean backupCompletedSuccessfully) {
        return backupCompletedSuccessfully
                ? R.string.backup_completed_successfully
                : R.string.backup_failed;
    }

    @StringRes
    private int getRestoreResultMessage(boolean restoreCompletedSuccessfully) {
        return restoreCompletedSuccessfully
                ? R.string.restore_completed_successfully
                : R.string.restore_failed;
    }

    private String getBackupSharedPreferencesName() {
        return backupPrefs.getFileName() + XML_EXTENSION;
    }

    private String getDatabaseSharedPreferencesName() {
        return databasePrefs.getFileName() + XML_EXTENSION;
    }

    private static String getDefaultSharedPreferencesName(@NonNull Context context) {
        return context.getPackageName() + "_preferences" + XML_EXTENSION;
    }
}
