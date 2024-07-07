package io.github.zwieback.familyfinance.business.article.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Article;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

class ArticleForceDestroyer extends EntityForceDestroyer<Article> {

    ArticleForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<Article> getEntityClass() {
        return Article.class;
    }

    @Override
    protected QueryAttribute<Article, Integer> getIdAttribute() {
        return Article.ID;
    }
}
