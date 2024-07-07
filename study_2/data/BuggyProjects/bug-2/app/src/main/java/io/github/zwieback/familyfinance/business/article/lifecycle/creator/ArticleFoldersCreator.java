package io.github.zwieback.familyfinance.business.article.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.model.Article;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class ArticleFoldersCreator extends ArticleCreator {

    public ArticleFoldersCreator(@NonNull Context context,
                                 @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Article> buildEntities() {
        Set<Article> articles = new TreeSet<>(this);
        articles.addAll(createIncomes());
        articles.addAll(createExpenses());
        return articles;
    }

    @NonNull
    private Set<Article> createIncomes() {
        Article root = findRoot(R.string.article_incomes);

        Set<Article> articles = new HashSet<>();
        articles.add(createIncomeFolder(root, R.string.article_bank));
        articles.add(createIncomeFolder(root, R.string.article_gifts));
        articles.add(createIncomeFolder(root, R.string.article_work));
        return articles;
    }

    @NonNull
    private Set<Article> createExpenses() {
        Article root = findRoot(R.string.article_expenses);

        Set<Article> articles = new HashSet<>();
        articles.add(createExpenseFolder(root, R.string.article_auto));
        articles.add(createExpenseFolder(root, R.string.article_home));
        articles.add(createExpenseFolder(root, R.string.article_clothes));
        articles.add(createExpenseFolder(root, R.string.article_products));
        articles.add(createExpenseFolder(root, R.string.article_transport));
        articles.add(createExpenseFolder(root, R.string.article_education));
        articles.add(createExpenseFolder(root, R.string.article_entertainment));
        articles.add(createExpenseFolder(root, R.string.article_beauty_and_health));
        return articles;
    }
}
