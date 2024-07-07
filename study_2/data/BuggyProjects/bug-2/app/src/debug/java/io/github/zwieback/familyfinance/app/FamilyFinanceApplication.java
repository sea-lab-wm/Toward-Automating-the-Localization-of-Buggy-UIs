package io.github.zwieback.familyfinance.app;

import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;

import io.github.zwieback.familyfinance.app.lifecycle.creator.DatabaseTableCreator;
import io.github.zwieback.familyfinance.app.lifecycle.destroyer.DatabaseDestroyer;
import io.github.zwieback.familyfinance.core.model.Models;
import io.requery.android.sqlite.DatabaseProvider;
import io.requery.android.sqlitex.SqlitexDatabaseSource;
import io.requery.sql.TableCreationMode;

public class FamilyFinanceApplication extends AbstractApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        turnOnStrictMode();
//        recreateDatabase();
    }

    @NonNull
    @Override
    protected DatabaseProvider buildDatabaseProvider() {
        // override onUpgrade to handle migrating to a new version
        DatabaseProvider source = new SqlitexDatabaseSource(this, Models.DEFAULT, DB_VERSION);
        // use this in development mode to drop and recreate the tables on every upgrade
        source.setLoggingEnabled(true);
        source.setTableCreationMode(TableCreationMode.CREATE_NOT_EXISTS);
        destroyViews(source.getConfiguration());
        createViews(source.getConfiguration());
        return source;
    }

    private void recreateDatabase() {
        new DatabaseDestroyer(this).deleteDatabases();
        new DatabaseTableCreator(this, getData()).createTables();
    }

    private void turnOnStrictMode() {
        StrictMode.enableDefaults();
    }
}
