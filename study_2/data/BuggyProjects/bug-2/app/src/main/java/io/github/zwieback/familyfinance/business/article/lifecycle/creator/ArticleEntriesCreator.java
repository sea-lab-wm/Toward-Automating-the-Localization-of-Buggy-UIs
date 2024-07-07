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

public class ArticleEntriesCreator extends ArticleCreator {

    public ArticleEntriesCreator(@NonNull Context context,
                                 @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Article> buildEntities() {
        Set<Article> articles = new TreeSet<>(this);
        articles.addAll(createIncomes());
        articles.addAll(createExpenses());
        articles.addAll(createServices());
        return articles;
    }

    @NonNull
    private Set<Article> createIncomes() {
        Article bank = findFolder(R.string.article_bank);
        Article work = findFolder(R.string.article_work);

        Set<Article> articles = new HashSet<>();
        articles.add(createIncomeEntry(bank, R.string.article_capitalization_of_interest));
        articles.add(createIncomeEntry(work, R.string.article_salary));
        articles.add(createIncomeEntry(work, R.string.article_bonus));
        return articles;
    }

    @NonNull
    private Set<Article> createExpenses() {
        Article auto = findFolder(R.string.article_auto);
        Article home = findFolder(R.string.article_home);
        Article clothes = findFolder(R.string.article_clothes);
        Article transport = findFolder(R.string.article_transport);
        Article education = findFolder(R.string.article_education);
        Article entertainment = findFolder(R.string.article_entertainment);

        Set<Article> articles = new HashSet<>();
        articles.add(createExpenseEntry(auto, R.string.article_fuel));
        articles.add(createExpenseEntry(auto, R.string.article_maintenance));
        articles.add(createExpenseEntry(home, R.string.article_rent));
        articles.add(createExpenseEntry(clothes, R.string.article_jacket));
        articles.add(createExpenseEntry(transport, R.string.article_taxi));
        articles.add(createExpenseEntry(transport, R.string.article_metro));
        articles.add(createExpenseEntry(education, R.string.article_foreign_language_courses));
        articles.add(createExpenseEntry(entertainment, R.string.article_cinema));
        articles.add(createExpenseEntry(entertainment, R.string.article_theater));
        return articles;
    }

    @NonNull
    private Set<Article> createServices() {
        Article root = findRoot(R.string.article_service);

        Set<Article> articles = new HashSet<>();
        articles.add(createServiceEntry(root, R.string.article_transfer));
        return articles;
    }
}
