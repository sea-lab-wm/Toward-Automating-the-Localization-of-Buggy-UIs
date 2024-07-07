package io.github.zwieback.familyfinance.business.article.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.article.adapter.IncomeArticleAdapter;
import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;

import static io.github.zwieback.familyfinance.business.article.filter.IncomeArticleFilter.INCOME_ARTICLE_FILTER;

public class IncomeArticleFragment extends ArticleFragment<IncomeArticleAdapter> {

    public static IncomeArticleFragment newInstance(ArticleFilter filter) {
        IncomeArticleFragment fragment = new IncomeArticleFragment();
        Bundle args = createArguments(INCOME_ARTICLE_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected IncomeArticleAdapter createEntityAdapter() {
        ArticleFilter filter = extractFilter(INCOME_ARTICLE_FILTER);
        return new IncomeArticleAdapter(context, clickListener, data, filter);
    }
}
