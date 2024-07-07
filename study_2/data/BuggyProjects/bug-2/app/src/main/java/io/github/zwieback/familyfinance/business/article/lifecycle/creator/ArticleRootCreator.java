package io.github.zwieback.familyfinance.business.article.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Set;
import java.util.TreeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.model.Article;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class ArticleRootCreator extends ArticleCreator {

    public ArticleRootCreator(@NonNull Context context,
                              @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Article> buildEntities() {
        Set<Article> articles = new TreeSet<>(this);
        articles.add(createIncomeFolder(null, R.string.article_incomes));
        articles.add(createExpenseFolder(null, R.string.article_expenses));
        articles.add(createServiceFolder(null, R.string.article_service));
        return articles;
    }
}
