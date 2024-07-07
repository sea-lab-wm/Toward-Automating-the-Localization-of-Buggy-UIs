package io.github.zwieback.familyfinance.business.account.lifecycle.destroyer;

import android.support.annotation.NonNull;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityViewDestroyer;

import java.sql.Connection;

public class AccountViewDestroyer extends EntityViewDestroyer {

    public AccountViewDestroyer(Connection connection) {
        super(connection);
    }

    @NonNull
    @Override
    protected String getViewName() {
        return "v_account";
    }
}
