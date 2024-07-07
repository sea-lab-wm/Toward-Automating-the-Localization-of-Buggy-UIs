package io.github.zwieback.familyfinance.business.currency.activity;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.currency.filter.CurrencyFilter;
import io.github.zwieback.familyfinance.business.currency.fragment.CurrencyFragment;
import io.github.zwieback.familyfinance.business.currency.lifecycle.destroyer.CurrencyFromAccountsDestroyer;
import io.github.zwieback.familyfinance.business.currency.listener.OnCurrencyClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.CurrencyView;

import static io.github.zwieback.familyfinance.business.currency.activity.CurrencyEditActivity.INPUT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.business.currency.filter.CurrencyFilter.CURRENCY_FILTER;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;

public class CurrencyActivity
        extends EntityActivity<CurrencyView, Currency, CurrencyFilter, CurrencyFragment>
        implements OnCurrencyClickListener {

    @Override
    protected int getTitleStringId() {
        return R.string.currency_activity_title;
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return CURRENCY_FILTER;
    }

    @NonNull
    @Override
    protected CurrencyFilter createDefaultFilter() {
        return new CurrencyFilter();
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_CURRENCY_ID;
    }

    @Override
    protected String getFragmentTag() {
        return getLocalClassName();
    }

    @Override
    protected CurrencyFragment createFragment() {
        return CurrencyFragment.newInstance(filter);
    }

    @Override
    protected void addEntity() {
        super.addEntity();
        Intent intent = new Intent(this, CurrencyEditActivity.class);
        startActivity(intent);
    }

    @Override
    protected void editEntity(CurrencyView currency) {
        super.editEntity(currency);
        Intent intent = new Intent(this, CurrencyEditActivity.class);
        intent.putExtra(INPUT_CURRENCY_ID, currency.getId());
        startActivity(intent);
    }

    @Override
    protected Class<Currency> getClassOfRegularEntity() {
        return Currency.class;
    }

    @Override
    protected EntityDestroyer<Currency> createDestroyer(CurrencyView entity) {
        return new CurrencyFromAccountsDestroyer(this, data);
    }
}
