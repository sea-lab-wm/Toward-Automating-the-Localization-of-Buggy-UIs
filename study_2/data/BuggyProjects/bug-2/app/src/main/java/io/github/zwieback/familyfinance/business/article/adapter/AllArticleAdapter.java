package io.github.zwieback.familyfinance.business.article.adapter;

import android.content.Context;

import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;
import io.github.zwieback.familyfinance.business.article.listener.OnArticleClickListener;
import io.github.zwieback.familyfinance.business.article.query.AllArticleQueryBuilder;
import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class AllArticleAdapter extends ArticleAdapter {

    public AllArticleAdapter(Context context,
                             OnArticleClickListener clickListener,
                             ReactiveEntityStore<Persistable> data,
                             ArticleFilter filter) {
        super(context, clickListener, data, filter);
    }

    @Override
    public Result<ArticleView> performQuery() {
        return AllArticleQueryBuilder.create(data)
                .setParentId(parentId)
                .setSearchName(filter.getSearchName())
                .build();
    }
}
