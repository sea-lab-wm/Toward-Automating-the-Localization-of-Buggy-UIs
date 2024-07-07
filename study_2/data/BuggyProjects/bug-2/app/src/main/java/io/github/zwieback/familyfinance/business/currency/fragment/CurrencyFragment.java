package io.github.zwieback.familyfinance.business.currency.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.currency.adapter.CurrencyAdapter;
import io.github.zwieback.familyfinance.business.currency.filter.CurrencyFilter;
import io.github.zwieback.familyfinance.business.currency.listener.OnCurrencyClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFragment;
import io.github.zwieback.familyfinance.core.model.CurrencyView;
import io.github.zwieback.familyfinance.databinding.ItemCurrencyBinding;

import static io.github.zwieback.familyfinance.business.currency.filter.CurrencyFilter.CURRENCY_FILTER;

public class CurrencyFragment extends EntityFragment<CurrencyView, CurrencyFilter,
        ItemCurrencyBinding, OnCurrencyClickListener, CurrencyAdapter> {

    public static CurrencyFragment newInstance(CurrencyFilter filter) {
        CurrencyFragment fragment = new CurrencyFragment();
        Bundle args = createArguments(CURRENCY_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected CurrencyAdapter createEntityAdapter() {
        CurrencyFilter filter = extractFilter(CURRENCY_FILTER);
        return new CurrencyAdapter(context, clickListener, data, filter);
    }
}
