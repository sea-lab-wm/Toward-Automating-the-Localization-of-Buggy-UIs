package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.business.article.activity.AllArticleActivity;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.preference.custom.EntityActivityResultPreference;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;

abstract class ArticlePreference extends EntityActivityResultPreference<Article> {

    ArticlePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    ArticlePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ArticlePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    ArticlePreference(Context context) {
        super(context);
    }

    @Override
    protected Intent getRequestIntent() {
        return new Intent(getContext(), AllArticleActivity.class);
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_ARTICLE_ID;
    }

    @Override
    protected Class<Article> getEntityClass() {
        return Article.class;
    }

    @Override
    protected String getEntityName(@NonNull Article article) {
        return article.getName();
    }
}
