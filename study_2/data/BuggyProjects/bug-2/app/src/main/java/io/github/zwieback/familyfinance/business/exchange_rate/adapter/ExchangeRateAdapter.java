package io.github.zwieback.familyfinance.business.exchange_rate.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter;
import io.github.zwieback.familyfinance.business.exchange_rate.listener.OnExchangeRateClickListener;
import io.github.zwieback.familyfinance.business.exchange_rate.query.ExchangeRateQueryBuilder;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.ExchangeRateView;
import io.github.zwieback.familyfinance.databinding.ItemExchangeRateBinding;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class ExchangeRateAdapter extends EntityAdapter<ExchangeRateView, ExchangeRateFilter,
        ItemExchangeRateBinding, OnExchangeRateClickListener> {

    public ExchangeRateAdapter(Context context,
                               OnExchangeRateClickListener clickListener,
                               ReactiveEntityStore<Persistable> data,
                               ExchangeRateFilter filter) {
        super(ExchangeRateView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<ExchangeRateView> createProvider(Context context) {
        return new ExchangeRateViewProvider(context);
    }

    @Override
    protected ItemExchangeRateBinding inflate(LayoutInflater inflater) {
        return ItemExchangeRateBinding.inflate(inflater);
    }

    @Override
    protected ExchangeRateView extractEntity(ItemExchangeRateBinding binding) {
        return (ExchangeRateView) binding.getExchangeRate();
    }

    @Override
    public Result<ExchangeRateView> performQuery() {
        return ExchangeRateQueryBuilder.create(data)
                .setCurrencyId(filter.getCurrencyId())
                .setStartDate(filter.getStartDate())
                .setEndDate(filter.getEndDate())
                .setStartValue(filter.getStartValue())
                .setEndValue(filter.getEndValue())
                .build();
    }

    @Override
    public void onBindViewHolder(ExchangeRateView exchangeRate,
                                 BindingHolder<ItemExchangeRateBinding> holder, int position) {
        holder.binding.setExchangeRate(exchangeRate);
        provider.setupIcon(holder.binding.icon.getIcon(), exchangeRate);
    }
}
