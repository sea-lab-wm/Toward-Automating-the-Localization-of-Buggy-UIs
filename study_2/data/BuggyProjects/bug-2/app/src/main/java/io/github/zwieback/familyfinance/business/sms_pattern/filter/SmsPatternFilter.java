package io.github.zwieback.familyfinance.business.sms_pattern.filter;

import android.os.Parcel;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;

public class SmsPatternFilter extends EntityFilter {

    public static final String SMS_PATTERN_FILTER = "smsPatternFilter";

    public static Creator<SmsPatternFilter> CREATOR = new Creator<SmsPatternFilter>() {

        public SmsPatternFilter createFromParcel(Parcel parcel) {
            return new SmsPatternFilter(parcel);
        }

        public SmsPatternFilter[] newArray(int size) {
            return new SmsPatternFilter[size];
        }
    };

    public SmsPatternFilter() {
        super();
    }

    public SmsPatternFilter(SmsPatternFilter filter) {
        super(filter);
    }

    private SmsPatternFilter(Parcel in) {
        super(in);
    }
}
