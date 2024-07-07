package io.github.zwieback.familyfinance.business.currency.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class CurrencyViewCreator extends EntityViewCreator {

    public CurrencyViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_currency";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT cu.id          AS id," +
                "       cu.icon_name   AS icon_name," +
                "       cu.name        AS name," +
                "       cu.description AS description" +
                "  FROM currency cu";
    }
}
