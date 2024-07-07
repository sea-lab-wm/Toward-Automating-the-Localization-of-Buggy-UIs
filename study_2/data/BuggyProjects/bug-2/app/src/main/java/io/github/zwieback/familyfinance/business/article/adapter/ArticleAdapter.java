package io.github.zwieback.familyfinance.business.article.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;
import io.github.zwieback.familyfinance.business.article.listener.OnArticleClickListener;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityFolderAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.github.zwieback.familyfinance.databinding.ItemArticleBinding;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class ArticleAdapter extends EntityFolderAdapter<ArticleView, ArticleFilter,
        ItemArticleBinding, OnArticleClickListener> {

    ArticleAdapter(Context context,
                   OnArticleClickListener clickListener,
                   ReactiveEntityStore<Persistable> data,
                   ArticleFilter filter) {
        super(ArticleView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<ArticleView> createProvider(Context context) {
        return new ArticleViewProvider(context);
    }

    @Override
    protected ItemArticleBinding inflate(LayoutInflater inflater) {
        return ItemArticleBinding.inflate(inflater);
    }

    @Override
    protected ArticleView extractEntity(ItemArticleBinding binding) {
        return (ArticleView) binding.getArticle();
    }

    @Override
    public void onBindViewHolder(ArticleView article, BindingHolder<ItemArticleBinding> holder,
                                 int position) {
        holder.binding.setArticle(article);
        provider.setupIcon(holder.binding.icon.getIcon(), article);
    }
}
