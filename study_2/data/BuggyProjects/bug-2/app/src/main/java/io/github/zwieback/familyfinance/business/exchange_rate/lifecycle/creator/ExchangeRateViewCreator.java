package io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class ExchangeRateViewCreator extends EntityViewCreator {

    public ExchangeRateViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_exchange_rate";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT er.id        AS id," +
                "       er.icon_name AS icon_name," +
                "       er._value    AS _value," +
                "       er._date     AS _date," +
                "       cu.id        AS currency_id," +
                "       cu.name      AS currency_name" +
                "  FROM exchange_rate er" +
                "       INNER JOIN currency cu ON cu.id = er.currency_id";
    }
}
