package io.github.zwieback.familyfinance.business.exchange_rate.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.exchange_rate.adapter.ExchangeRateAdapter;
import io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter;
import io.github.zwieback.familyfinance.business.exchange_rate.listener.OnExchangeRateClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFragment;
import io.github.zwieback.familyfinance.core.model.ExchangeRateView;
import io.github.zwieback.familyfinance.databinding.ItemExchangeRateBinding;

import static io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter.EXCHANGE_RATE_FILTER;

public class ExchangeRateFragment extends EntityFragment<ExchangeRateView, ExchangeRateFilter,
        ItemExchangeRateBinding, OnExchangeRateClickListener, ExchangeRateAdapter> {

    public static ExchangeRateFragment newInstance(ExchangeRateFilter filter) {
        ExchangeRateFragment fragment = new ExchangeRateFragment();
        Bundle args = createArguments(EXCHANGE_RATE_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected ExchangeRateAdapter createEntityAdapter() {
        ExchangeRateFilter filter = extractFilter(EXCHANGE_RATE_FILTER);
        return new ExchangeRateAdapter(context, clickListener, data, filter);
    }
}
