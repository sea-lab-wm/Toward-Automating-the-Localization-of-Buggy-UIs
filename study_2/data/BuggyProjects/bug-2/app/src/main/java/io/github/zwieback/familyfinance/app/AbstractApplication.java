package io.github.zwieback.familyfinance.app;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import java.sql.Connection;

import io.github.zwieback.familyfinance.app.lifecycle.creator.DatabaseViewCreator;
import io.github.zwieback.familyfinance.app.lifecycle.destroyer.DatabaseViewDestroyer;
import io.github.zwieback.familyfinance.core.database.exception.SQLException;
import io.requery.Persistable;
import io.requery.android.sqlite.DatabaseProvider;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.EntityDataStore;

public abstract class AbstractApplication extends MultiDexApplication {

    protected static final int DB_VERSION = 6;

    private ReactiveEntityStore<Persistable> dataStore;

    /**
     * @return {@link EntityDataStore} single instance for the application.
     * <p/>
     * Note if you're using Dagger you can make this part of your application level module returning
     * {@code @Provides @Singleton}.
     */
    @NonNull
    public final ReactiveEntityStore<Persistable> getData() {
        if (dataStore == null) {
            DatabaseProvider databaseProvider = buildDatabaseProvider();
            Configuration configuration = databaseProvider.getConfiguration();
            dataStore = ReactiveSupport.toReactiveStore(
                    new EntityDataStore<>(configuration));
        }
        return dataStore;
    }

    @NonNull
    protected abstract DatabaseProvider buildDatabaseProvider();

    /**
     * Workaround for creating views, while requery does not support creating views.
     * <p>
     * Note: This method must be called before the creation of the {@link #dataStore},
     * otherwise {@link java.sql.SQLException} will be thrown.
     *
     * @see <a href="https://github.com/requery/requery/issues/721#issuecomment-344153774">
     * Using SQLite views
     * </a>
     */
    protected final void createViews(Configuration configuration) {
        try (Connection connection = configuration.getConnectionProvider().getConnection()) {
            new DatabaseViewCreator(connection).createViews();
        } catch (java.sql.SQLException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    /**
     * Workaround for destroying views, while requery does not support destroying views.
     * <p>
     * Note: This method must be called before the creation of the {@link #dataStore},
     * otherwise {@link java.sql.SQLException} will be thrown.
     *
     * @see <a href="https://github.com/requery/requery/issues/721#issuecomment-344153774">
     * Using SQLite views
     * </a>
     */
    protected final void destroyViews(Configuration configuration) {
        try (Connection connection = configuration.getConnectionProvider().getConnection()) {
            new DatabaseViewDestroyer(connection).destroyViews();
        } catch (java.sql.SQLException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }
}
