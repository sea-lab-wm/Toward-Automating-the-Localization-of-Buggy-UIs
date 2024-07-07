package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.preference.custom.EntityActivityResultPreference;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;

public class CurrencyPreference extends EntityActivityResultPreference<Currency> {

    @SuppressWarnings("unused")
    public CurrencyPreference(Context context, AttributeSet attrs, int defStyleAttr,
                              int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public CurrencyPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public CurrencyPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public CurrencyPreference(Context context) {
        super(context);
    }

    @Override
    protected int getRequestCode() {
        return CURRENCY_CODE;
    }

    @Override
    protected Intent getRequestIntent() {
        return new Intent(getContext(), CurrencyActivity.class);
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_CURRENCY_ID;
    }

    @Override
    protected int getSavedEntityId() {
        return databasePrefs.getCurrencyId();
    }

    @Override
    protected void saveEntityId(int currencyId) {
        databasePrefs.setCurrencyId(currencyId);
    }

    @Override
    protected Class<Currency> getEntityClass() {
        return Currency.class;
    }

    @Override
    protected String getEntityName(@NonNull Currency currency) {
        return currency.getName();
    }

    @Override
    protected int getPreferenceTitleRes() {
        return R.string.currency_id_preference_title;
    }
}
