package io.github.zwieback.familyfinance.business.article.query;

import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class AllArticleQueryBuilder extends ArticleQueryBuilder<AllArticleQueryBuilder> {

    public static AllArticleQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new AllArticleQueryBuilder(data);
    }

    private AllArticleQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }
}
