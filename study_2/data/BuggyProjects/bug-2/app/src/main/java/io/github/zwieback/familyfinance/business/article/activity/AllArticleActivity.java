package io.github.zwieback.familyfinance.business.article.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.filter.AllArticleFilter;
import io.github.zwieback.familyfinance.business.article.fragment.AllArticleFragment;

import static io.github.zwieback.familyfinance.business.article.filter.AllArticleFilter.ALL_ARTICLE_FILTER;

public class AllArticleActivity extends ArticleActivity<AllArticleFragment, AllArticleFilter> {

    @Nullable
    @Override
    Integer getInitialParentId() {
        return null;
    }

    @Override
    protected int getTitleStringId() {
        return R.string.all_article_activity_title;
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return ALL_ARTICLE_FILTER;
    }

    @NonNull
    @Override
    protected AllArticleFilter createDefaultFilter() {
        AllArticleFilter filter = new AllArticleFilter();
        filter.setParentId(getInitialParentId());
        return filter;
    }

    @Override
    protected AllArticleFragment createFragment() {
        return AllArticleFragment.newInstance(filter);
    }
}
