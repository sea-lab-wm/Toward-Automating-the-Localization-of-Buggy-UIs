package io.github.zwieback.familyfinance.business.article.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.article.adapter.AllArticleAdapter;
import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;

import static io.github.zwieback.familyfinance.business.article.filter.AllArticleFilter.ALL_ARTICLE_FILTER;

public class AllArticleFragment extends ArticleFragment<AllArticleAdapter> {

    public static AllArticleFragment newInstance(ArticleFilter filter) {
        AllArticleFragment fragment = new AllArticleFragment();
        Bundle args = createArguments(ALL_ARTICLE_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected AllArticleAdapter createEntityAdapter() {
        ArticleFilter filter = extractFilter(ALL_ARTICLE_FILTER);
        return new AllArticleAdapter(context, clickListener, data, filter);
    }
}
