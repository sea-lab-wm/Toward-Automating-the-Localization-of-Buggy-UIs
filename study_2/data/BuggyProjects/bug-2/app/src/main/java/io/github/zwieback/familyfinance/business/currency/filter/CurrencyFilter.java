package io.github.zwieback.familyfinance.business.currency.filter;

import android.os.Parcel;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;

public class CurrencyFilter extends EntityFilter {

    public static final String CURRENCY_FILTER = "currencyFilter";

    public static Creator<CurrencyFilter> CREATOR = new Creator<CurrencyFilter>() {

        public CurrencyFilter createFromParcel(Parcel parcel) {
            return new CurrencyFilter(parcel);
        }

        public CurrencyFilter[] newArray(int size) {
            return new CurrencyFilter[size];
        }
    };

    public CurrencyFilter() {
        super();
    }

    public CurrencyFilter(CurrencyFilter filter) {
        super(filter);
    }

    private CurrencyFilter(Parcel in) {
        super(in);
    }
}
