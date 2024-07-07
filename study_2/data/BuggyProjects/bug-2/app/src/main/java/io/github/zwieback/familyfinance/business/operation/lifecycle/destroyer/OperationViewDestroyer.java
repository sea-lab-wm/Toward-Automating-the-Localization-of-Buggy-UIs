package io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityViewDestroyer;

public class OperationViewDestroyer extends EntityViewDestroyer {

    public OperationViewDestroyer(Connection connection) {
        super(connection);
    }

    @NonNull
    @Override
    protected String getViewName() {
        return "v_operation";
    }
}
