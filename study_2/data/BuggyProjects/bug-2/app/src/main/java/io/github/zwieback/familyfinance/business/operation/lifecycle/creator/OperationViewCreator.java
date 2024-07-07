package io.github.zwieback.familyfinance.business.operation.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class OperationViewCreator extends EntityViewCreator {

    public OperationViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    @Override
    protected String getViewName() {
        return "v_operation";
    }

    @NonNull
    @Override
    protected String getViewBody() {
        return " SELECT op.id                           AS id," +
                "       COALESCE(op.icon_name, ac.icon_name)" +
                "                                       AS icon_name," +
                "       op.linked_transfer_operation_id AS linked_transfer_operation_id," +
                "       ac.id                           AS account_id," +
                "       ac.name                         AS account_name," +
                "       ar.id                           AS article_id," +
                "       ar.name                         AS article_name," +
                "       ap.id                           AS article_parent_id," +
                "       ap.name                         AS article_parent_name," +
                "       pe.id                           AS owner_id," +
                "       pe.name                         AS owner_name," +
                "       er.id                           AS exchange_rate_id," +
                "       er._value                       AS exchange_rate_value," +
                "       cu.id                           AS currency_id," +
                "       cu.name                         AS currency_name," +
                "       op._type                        AS _type," +
                "       op._date                        AS _date," +
                "       op._value                       AS _value," +
                "       op.description                  AS description," +
                "       op.url                          AS url" +
                "  FROM operation op" +
                "       INNER JOIN account ac ON ac.id = op.account_id" +
                "       INNER JOIN article ar ON ar.id = op.article_id" +
                "        LEFT JOIN article ap ON ap.id = ar.parent_id" +
                "       INNER JOIN person pe ON pe.id = op.owner_id" +
                "       INNER JOIN exchange_rate er ON er.id = op.exchange_rate_id" +
                "       INNER JOIN currency cu ON cu.id = er.currency_id";
    }
}
