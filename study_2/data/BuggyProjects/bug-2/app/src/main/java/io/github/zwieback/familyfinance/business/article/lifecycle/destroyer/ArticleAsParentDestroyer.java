package io.github.zwieback.familyfinance.business.article.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityAsParentDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Article;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.reactivex.ReactiveEntityStore;

public class ArticleAsParentDestroyer extends EntityAsParentDestroyer<Article> {

    public ArticleAsParentDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Article> next() {
        return new ArticleFromExpenseOperationsDestroyer(context, data);
    }

    @Override
    protected Class<Article> getEntityClass() {
        return Article.class;
    }

    @Override
    protected QueryExpression<Integer> getParentIdExpression() {
        return Article.PARENT_ID;
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.articles_with_parent_exists;
    }
}
