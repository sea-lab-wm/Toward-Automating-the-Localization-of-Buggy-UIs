package io.github.zwieback.familyfinance.business.article.query;

import io.github.zwieback.familyfinance.core.model.type.ArticleType;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class ExpenseArticleQueryBuilder extends ArticleQueryBuilder<ExpenseArticleQueryBuilder> {

    public static ExpenseArticleQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new ExpenseArticleQueryBuilder(data)
                .setType(ArticleType.EXPENSE_ARTICLE);
    }

    private ExpenseArticleQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
