package io.github.zwieback.familyfinance.business.article.lifecycle.destroyer;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromPreferencesDestroyer;
import io.github.zwieback.familyfinance.core.model.Article;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

class ArticleFromPreferencesDestroyer extends EntityFromPreferencesDestroyer<Article> {

    ArticleFromPreferencesDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Article> next() {
        return new ArticleForceDestroyer(context, data);
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.preferences_contains_article;
    }

    @Override
    protected boolean preferencesContainsEntity(Article article) {
        List<Integer> articleIds = Arrays.asList(
                databasePrefs.getIncomesArticleId(),
                databasePrefs.getExpensesArticleId(),
                databasePrefs.getTransferArticleId());
        return articleIds.contains(article.getId());
    }
}
