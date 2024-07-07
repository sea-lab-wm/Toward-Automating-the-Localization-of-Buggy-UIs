package io.github.zwieback.familyfinance.business.sms_pattern.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.sms_pattern.adapter.SmsPatternAdapter;
import io.github.zwieback.familyfinance.business.sms_pattern.filter.SmsPatternFilter;
import io.github.zwieback.familyfinance.business.sms_pattern.listener.OnSmsPatternClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFragment;
import io.github.zwieback.familyfinance.core.model.SmsPatternView;
import io.github.zwieback.familyfinance.databinding.ItemSmsPatternBinding;

import static io.github.zwieback.familyfinance.business.sms_pattern.filter.SmsPatternFilter.SMS_PATTERN_FILTER;

public class SmsPatternFragment extends EntityFragment<SmsPatternView, SmsPatternFilter,
        ItemSmsPatternBinding, OnSmsPatternClickListener, SmsPatternAdapter> {

    public static SmsPatternFragment newInstance(SmsPatternFilter filter) {
        SmsPatternFragment fragment = new SmsPatternFragment();
        Bundle args = createArguments(SMS_PATTERN_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected SmsPatternAdapter createEntityAdapter() {
        SmsPatternFilter filter = extractFilter(SMS_PATTERN_FILTER);
        return new SmsPatternAdapter(context, clickListener, data, filter);
    }
}
