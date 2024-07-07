package io.github.zwieback.familyfinance.business.template.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class TemplateViewCreator extends EntityViewCreator {

    public TemplateViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_template";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT te.id          AS id," +
                "       te.icon_name   AS icon_name," +
                "       te.name        AS name," +
                "       ac.id          AS account_id," +
                "       ac.name        AS account_name," +
                "       at.id          AS transfer_account_id," +
                "       at.name        AS transfer_account_name," +
                "       ar.id          AS article_id," +
                "       ar.name        AS article_name," +
                "       ap.id          AS article_parent_id," +
                "       ap.name        AS article_parent_name," +
                "       pe.id          AS owner_id," +
                "       pe.name        AS owner_name," +
                "       er.id          AS exchange_rate_id," +
                "       er._value      AS exchange_rate_value," +
                "       cu.id          AS currency_id," +
                "       cu.name        AS currency_name," +
                "       te._type       AS _type," +
                "       te._date       AS _date," +
                "       te._value      AS _value," +
                "       te.description AS description," +
                "       te.url         AS url" +
                "  FROM t_template te" +
                "       LEFT JOIN account ac ON ac.id = te.account_id" +
                "       LEFT JOIN account at ON at.id = te.transfer_account_id" +
                "       LEFT JOIN article ar ON ar.id = te.article_id" +
                "       LEFT JOIN article ap ON ap.id = ar.parent_id" +
                "       LEFT JOIN person  pe ON pe.id = te.owner_id" +
                "       LEFT JOIN exchange_rate er ON er.id = te.exchange_rate_id" +
                "       LEFT JOIN currency cu ON cu.id = er.currency_id";
    }
}
