package io.github.zwieback.familyfinance.business.article.fragment;

import io.github.zwieback.familyfinance.business.article.adapter.ArticleAdapter;
import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;
import io.github.zwieback.familyfinance.business.article.listener.OnArticleClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFolderFragment;
import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.github.zwieback.familyfinance.databinding.ItemArticleBinding;

public abstract class ArticleFragment<ADAPTER extends ArticleAdapter>
        extends EntityFolderFragment<ArticleView, ArticleFilter, ItemArticleBinding,
        OnArticleClickListener, ADAPTER> {
}
