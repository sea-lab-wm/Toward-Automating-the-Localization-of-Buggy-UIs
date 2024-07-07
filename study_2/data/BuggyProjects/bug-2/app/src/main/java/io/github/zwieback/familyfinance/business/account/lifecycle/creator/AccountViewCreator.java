package io.github.zwieback.familyfinance.business.account.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class AccountViewCreator extends EntityViewCreator {

    public AccountViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_account";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT ac.id              AS id," +
                "       ac.icon_name       AS icon_name," +
                "       ac.is_folder       AS is_folder," +
                "       ac.active          AS active," +
                "       ac.name            AS name," +
                "       ac.initial_balance AS initial_balance," +
                "       ac.order_code      AS order_code," +
                "       ap.id              AS parent_id," +
                "       ap.name            AS parent_name," +
                "       cu.id              AS currency_id," +
                "       cu.name            AS currency_name," +
                "       pe.id              AS owner_id," +
                "       pe.name            AS owner_name," +
                "       ac._type           AS _type," +
                "       ac._number         AS _number," +
                "       ac.payment_system  AS payment_system," +
                "       ac.card_number     AS card_number" +
                "  FROM account ac" +
                "       LEFT JOIN account ap  ON ap.id = ac.parent_id" +
                "       LEFT JOIN currency cu ON cu.id = ac.currency_id" +
                "       LEFT JOIN person pe   ON pe.id = ac.owner_id";
    }
}
