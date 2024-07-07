package io.github.zwieback.familyfinance.business.sms_pattern.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.sms_pattern.filter.SmsPatternFilter;
import io.github.zwieback.familyfinance.business.sms_pattern.fragment.SmsPatternFragment;
import io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.destroyer.SmsPatternForceDestroyer;
import io.github.zwieback.familyfinance.business.sms_pattern.listener.OnSmsPatternClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.github.zwieback.familyfinance.core.model.SmsPatternView;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_SMS_PATTERN_ID;
import static io.github.zwieback.familyfinance.business.sms_pattern.filter.SmsPatternFilter.SMS_PATTERN_FILTER;

public class SmsPatternActivity
        extends EntityActivity<SmsPatternView, SmsPattern, SmsPatternFilter, SmsPatternFragment>
        implements OnSmsPatternClickListener {

    @Override
    protected int getTitleStringId() {
        return R.string.sms_pattern_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return SMS_PATTERN_FILTER;
    }

    @NonNull
    @Override
    protected SmsPatternFilter createDefaultFilter() {
        return new SmsPatternFilter();
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_SMS_PATTERN_ID;
    }

    @Override
    protected String getFragmentTag() {
        return getLocalClassName();
    }

    @Override
    protected SmsPatternFragment createFragment() {
        return SmsPatternFragment.newInstance(filter);
    }

    @Override
    protected void addEntity() {
        super.addEntity();
        Intent intent = new Intent(this, SmsPatternEditActivity.class);
        startActivity(intent);
    }

    @Override
    protected void editEntity(SmsPatternView smsPattern) {
        super.editEntity(smsPattern);
        Intent intent = new Intent(this, SmsPatternEditActivity.class);
        intent.putExtra(SmsPatternEditActivity.INPUT_SMS_PATTERN_ID, smsPattern.getId());
        startActivity(intent);
    }

    @Override
    protected Class<SmsPattern> getClassOfRegularEntity() {
        return SmsPattern.class;
    }

    @Override
    protected EntityDestroyer<SmsPattern> createDestroyer(SmsPatternView smsPattern) {
        return new SmsPatternForceDestroyer(this, data);
    }
}
