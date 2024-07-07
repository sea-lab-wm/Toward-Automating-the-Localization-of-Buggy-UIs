package io.github.zwieback.familyfinance.business.template.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import io.github.zwieback.familyfinance.business.template.filter.TemplateFilter;
import io.github.zwieback.familyfinance.business.template.listener.OnTemplateClickListener;
import io.github.zwieback.familyfinance.business.template.query.TemplateQueryBuilder;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.github.zwieback.familyfinance.databinding.ItemTemplateBinding;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class TemplateAdapter extends EntityAdapter<TemplateView, TemplateFilter,
        ItemTemplateBinding, OnTemplateClickListener> {

    public TemplateAdapter(Context context,
                           OnTemplateClickListener clickListener,
                           ReactiveEntityStore<Persistable> data,
                           TemplateFilter filter) {
        super(TemplateView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<TemplateView> createProvider(Context context) {
        return new TemplateViewProvider(context);
    }

    @Override
    protected ItemTemplateBinding inflate(LayoutInflater inflater) {
        return ItemTemplateBinding.inflate(inflater);
    }

    @Override
    protected TemplateView extractEntity(ItemTemplateBinding binding) {
        return (TemplateView) binding.getTemplate();
    }

    @Override
    public Result<TemplateView> performQuery() {
        return TemplateQueryBuilder.create(data)
                .build();
    }

    @Override
    public void onBindViewHolder(TemplateView template, BindingHolder<ItemTemplateBinding> holder,
                                 int position) {
        holder.binding.setTemplate(template);
        provider.setupIcon(holder.binding.icon.getIcon(), template);
    }
}
