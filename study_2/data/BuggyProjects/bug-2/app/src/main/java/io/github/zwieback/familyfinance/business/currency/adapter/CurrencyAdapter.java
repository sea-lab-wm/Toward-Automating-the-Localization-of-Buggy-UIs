package io.github.zwieback.familyfinance.business.currency.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import io.github.zwieback.familyfinance.business.currency.filter.CurrencyFilter;
import io.github.zwieback.familyfinance.business.currency.listener.OnCurrencyClickListener;
import io.github.zwieback.familyfinance.business.currency.query.CurrencyQueryBuilder;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.CurrencyView;
import io.github.zwieback.familyfinance.databinding.ItemCurrencyBinding;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class CurrencyAdapter extends EntityAdapter<CurrencyView, CurrencyFilter,
        ItemCurrencyBinding, OnCurrencyClickListener> {

    public CurrencyAdapter(Context context,
                           OnCurrencyClickListener clickListener,
                           ReactiveEntityStore<Persistable> data,
                           CurrencyFilter filter) {
        super(CurrencyView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<CurrencyView> createProvider(Context context) {
        return new CurrencyViewProvider(context);
    }

    @Override
    protected ItemCurrencyBinding inflate(LayoutInflater inflater) {
        return ItemCurrencyBinding.inflate(inflater);
    }

    @Override
    protected CurrencyView extractEntity(ItemCurrencyBinding binding) {
        return (CurrencyView) binding.getCurrency();
    }

    @Override
    public Result<CurrencyView> performQuery() {
        return CurrencyQueryBuilder.create(data)
                .build();
    }

    @Override
    public void onBindViewHolder(CurrencyView currency, BindingHolder<ItemCurrencyBinding> holder,
                                 int position) {
        holder.binding.setCurrency(currency);
        provider.setupIcon(holder.binding.icon.getIcon(), currency);
    }
}
