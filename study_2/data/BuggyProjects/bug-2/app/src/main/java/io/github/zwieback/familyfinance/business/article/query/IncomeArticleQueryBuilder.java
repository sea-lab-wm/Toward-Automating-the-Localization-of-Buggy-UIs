package io.github.zwieback.familyfinance.business.article.query;

import io.github.zwieback.familyfinance.core.model.type.ArticleType;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class IncomeArticleQueryBuilder extends ArticleQueryBuilder<IncomeArticleQueryBuilder> {

    public static IncomeArticleQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new IncomeArticleQueryBuilder(data)
                .setType(ArticleType.INCOME_ARTICLE);
    }

    private IncomeArticleQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
