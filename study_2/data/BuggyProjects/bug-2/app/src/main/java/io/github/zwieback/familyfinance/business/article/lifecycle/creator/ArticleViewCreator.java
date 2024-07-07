package io.github.zwieback.familyfinance.business.article.lifecycle.creator;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;

public class ArticleViewCreator extends EntityViewCreator {

    public ArticleViewCreator(Connection connection) {
        super(connection);
    }

    @NonNull
    protected String getViewName() {
        return "v_article";
    }

    @NonNull
    protected String getViewBody() {
        return " SELECT ar.id            AS id," +
                "       ar.icon_name     AS icon_name," +
                "       ar.is_folder     AS is_folder," +
                "       ar._type         AS _type," +
                "       ar.name          AS name," +
                "       ar.name_ascii    AS name_ascii," +
                "       ar.default_value AS default_value," +
                "       ap.id            AS parent_id," +
                "       ap.name          AS parent_name" +
                "  FROM article ar" +
                "       LEFT JOIN article ap ON ap.id = ar.parent_id";
    }
}
