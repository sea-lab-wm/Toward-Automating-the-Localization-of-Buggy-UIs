package io.github.zwieback.familyfinance.core.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

public abstract class EntityViewCreator implements Callable<Observable<Boolean>> {

    private final Connection connection;

    protected EntityViewCreator(Connection connection) {
        this.connection = connection;
    }

    @NonNull
    protected abstract String getViewName();

    @NonNull
    protected abstract String getViewBody();

    /**
     * @return {@code false} because "CREATE VIEW" is DDL operation
     */
    @Override
    public Observable<Boolean> call() {
        return Observable.fromCallable(() -> {
            try (Statement statement = connection.createStatement()) {
                return statement.execute("CREATE VIEW IF NOT EXISTS " + getViewName() + " AS " +
                        getViewBody());
            }
        });
    }
}
