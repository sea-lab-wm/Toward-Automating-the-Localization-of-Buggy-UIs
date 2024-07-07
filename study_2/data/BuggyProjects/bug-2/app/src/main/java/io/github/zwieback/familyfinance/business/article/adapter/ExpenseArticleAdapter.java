package io.github.zwieback.familyfinance.business.article.adapter;

import android.content.Context;

import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;
import io.github.zwieback.familyfinance.business.article.listener.OnArticleClickListener;
import io.github.zwieback.familyfinance.business.article.query.ExpenseArticleQueryBuilder;
import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class ExpenseArticleAdapter extends ArticleAdapter {

    public ExpenseArticleAdapter(Context context,
                                 OnArticleClickListener clickListener,
                                 ReactiveEntityStore<Persistable> data,
                                 ArticleFilter filter) {
        super(context, clickListener, data, filter);
    }

    @Override
    public Result<ArticleView> performQuery() {
        return ExpenseArticleQueryBuilder.create(data)
                .setParentId(parentId)
                .setSearchName(filter.getSearchName())
                .build();
    }
}
