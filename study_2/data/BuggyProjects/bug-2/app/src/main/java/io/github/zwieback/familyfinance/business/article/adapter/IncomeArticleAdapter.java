package io.github.zwieback.familyfinance.business.article.adapter;

import android.content.Context;

import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;
import io.github.zwieback.familyfinance.business.article.listener.OnArticleClickListener;
import io.github.zwieback.familyfinance.business.article.query.IncomeArticleQueryBuilder;
import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class IncomeArticleAdapter extends ArticleAdapter {

    public IncomeArticleAdapter(Context context,
                                OnArticleClickListener clickListener,
                                ReactiveEntityStore<Persistable> data,
                                ArticleFilter filter) {
        super(context, clickListener, data, filter);
    }

    @Override
    public Result<ArticleView> performQuery() {
        return IncomeArticleQueryBuilder.create(data)
                .setParentId(parentId)
                .setSearchName(filter.getSearchName())
                .build();
    }
}
