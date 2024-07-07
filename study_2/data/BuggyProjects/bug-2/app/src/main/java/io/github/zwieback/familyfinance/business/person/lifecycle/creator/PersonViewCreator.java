package io.github.zwieback.familyfinance.business.person.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class PersonViewCreator extends EntityViewCreator {

    public PersonViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_person";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT pe.id         AS id," +
                "       pe.icon_name  AS icon_name," +
                "       pe.is_folder  AS is_folder," +
                "       pe.name       AS name," +
                "       pe.order_code AS order_code," +
                "       pp.id         AS parent_id," +
                "       pp.name       AS parent_name" +
                "  FROM person pe" +
                "       LEFT JOIN person pp ON pp.id = pe.parent_id";
    }
}
