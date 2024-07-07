package io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.destroyer;

import android.support.annotation.NonNull;

import java.sql.Connection;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityViewDestroyer;

public class SmsPatternViewDestroyer extends EntityViewDestroyer {

    public SmsPatternViewDestroyer(Connection connection) {
        super(connection);
    }

    @NonNull
    @Override
    protected String getViewName() {
        return "v_sms_pattern";
    }
}
