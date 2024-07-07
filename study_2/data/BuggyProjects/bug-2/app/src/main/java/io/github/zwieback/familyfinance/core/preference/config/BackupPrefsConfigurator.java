package io.github.zwieback.familyfinance.core.preference.config;

import ds.gendalf.PrefsConfig;

@PrefsConfig("BackupPrefs")
public interface BackupPrefsConfigurator {

    String backupPath = null;
}
