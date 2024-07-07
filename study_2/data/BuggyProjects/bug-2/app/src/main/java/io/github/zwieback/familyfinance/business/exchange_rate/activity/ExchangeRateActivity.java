package io.github.zwieback.familyfinance.business.exchange_rate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.exchange_rate.dialog.ExchangeRateFilterDialog;
import io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter;
import io.github.zwieback.familyfinance.business.exchange_rate.fragment.ExchangeRateFragment;
import io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.destroyer.ExchangeRateFromExpenseOperationsDestroyer;
import io.github.zwieback.familyfinance.business.exchange_rate.listener.OnExchangeRateClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.ExchangeRateView;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_EXCHANGE_RATE_ID;
import static io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter.EXCHANGE_RATE_FILTER;

public class ExchangeRateActivity
        extends EntityActivity<ExchangeRateView, ExchangeRate, ExchangeRateFilter,
        ExchangeRateFragment>
        implements OnExchangeRateClickListener {

    public static final String INPUT_CURRENCY_ID = "currencyId";

    @Override
    protected int getTitleStringId() {
        return R.string.exchange_rate_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        Integer currencyId = extractId(getIntent().getExtras(), INPUT_CURRENCY_ID);
        if (currencyId != null) {
            filter.setCurrencyId(currencyId);
        }
    }

    @Override
    protected boolean addFilterMenuItem() {
        return true;
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return EXCHANGE_RATE_FILTER;
    }

    @NonNull
    @Override
    protected ExchangeRateFilter createDefaultFilter() {
        ExchangeRateFilter filter = new ExchangeRateFilter();
        filter.setCurrencyId(databasePrefs.getCurrencyId());
        return filter;
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_EXCHANGE_RATE_ID;
    }

    @Override
    protected String getFragmentTag() {
        return String.format("%s", getLocalClassName());
    }

    @Override
    protected ExchangeRateFragment createFragment() {
        return ExchangeRateFragment.newInstance(filter);
    }

    @Override
    protected void addEntity() {
        super.addEntity();
        Intent intent = new Intent(this, ExchangeRateEditActivity.class);
        intent.putExtra(ExchangeRateEditActivity.INPUT_CURRENCY_ID, filter.getCurrencyId());
        startActivity(intent);
    }

    @Override
    protected void editEntity(ExchangeRateView exchangeRate) {
        super.editEntity(exchangeRate);
        Intent intent = new Intent(this, ExchangeRateEditActivity.class);
        intent.putExtra(ExchangeRateEditActivity.INPUT_EXCHANGE_RATE_ID, exchangeRate.getId());
        startActivity(intent);
    }

    @Override
    protected Class<ExchangeRate> getClassOfRegularEntity() {
        return ExchangeRate.class;
    }

    @Override
    protected EntityDestroyer<ExchangeRate> createDestroyer(ExchangeRateView exchangeRateView) {
        return new ExchangeRateFromExpenseOperationsDestroyer(this, data);
    }

    @Override
    protected void showFilterDialog() {
        DialogFragment dialog = ExchangeRateFilterDialog.newInstance(filter);
        dialog.show(getSupportFragmentManager(), "ExchangeRateFilterDialog");
    }
}
