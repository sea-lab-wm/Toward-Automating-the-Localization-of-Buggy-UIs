package io.github.zwieback.familyfinance.business.article.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.function.Function;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.type.ArticleType;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static io.github.zwieback.familyfinance.core.model.type.ArticleType.EXPENSE_ARTICLE;
import static io.github.zwieback.familyfinance.core.model.type.ArticleType.INCOME_ARTICLE;
import static io.github.zwieback.familyfinance.core.model.type.ArticleType.SERVICE_ARTICLE;
import static io.github.zwieback.familyfinance.util.TransliterationUtils.transliterate;

abstract class ArticleCreator extends EntityCreator<Article> {

    ArticleCreator(@NonNull Context context,
                   @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    /**
     * Do not remove {@link SuppressWarnings} due to next link:
     * <a href="https://github.com/aNNiMON/Lightweight-Stream-API/issues/148">
     * Gradle compile error when using ComparatorCompat
     * </a>
     */
    @SuppressWarnings("RedundantCast")
    @Override
    public int compare(Article left, Article right) {
        return ComparatorCompat.comparing(Article::getType)
                .thenComparing((Function<Article, String>) Article::getName)
                .compare(left, right);
    }

    @NonNull
    Article findRoot(@StringRes int nameId) {
        String name = getString(nameId);
        return data
                .select(Article.class)
                .where(Article.NAME.eq(name).and(Article.PARENT_ID.isNull()))
                .get().first();
    }

    @NonNull
    Article findFolder(@StringRes int nameId) {
        String name = getString(nameId);
        return data
                .select(Article.class)
                .where(Article.NAME.eq(name).and(Article.PARENT_ID.notNull()))
                .get().first();
    }

    @NonNull
    Article createIncomeFolder(@Nullable Article parent, @StringRes int nameId) {
        return createArticle(parent, INCOME_ARTICLE, nameId, true);
    }

    @NonNull
    Article createIncomeEntry(@Nullable Article parent, @StringRes int nameId) {
        return createArticle(parent, INCOME_ARTICLE, nameId, false);
    }

    @NonNull
    Article createExpenseFolder(@Nullable Article parent, @StringRes int nameId) {
        return createArticle(parent, EXPENSE_ARTICLE, nameId, true);
    }

    @NonNull
    Article createExpenseEntry(@Nullable Article parent, @StringRes int nameId) {
        return createArticle(parent, EXPENSE_ARTICLE, nameId, false);
    }

    @SuppressWarnings("SameParameterValue")
    @NonNull
    Article createServiceFolder(@Nullable Article parent, @StringRes int nameId) {
        return createArticle(parent, SERVICE_ARTICLE, nameId, true);
    }

    @NonNull
    Article createServiceEntry(@Nullable Article parent, @StringRes int nameId) {
        return createArticle(parent, SERVICE_ARTICLE, nameId, false);
    }

    @NonNull
    private Article createArticle(@Nullable Article parent,
                                  @NonNull ArticleType type,
                                  @StringRes int nameId,
                                  boolean folder) {
        String name = getString(nameId);
        return new Article()
                .setParent(parent)
                .setType(type)
                .setName(name)
                .setNameAscii(transliterate(name))
                .setFolder(folder);
    }
}
