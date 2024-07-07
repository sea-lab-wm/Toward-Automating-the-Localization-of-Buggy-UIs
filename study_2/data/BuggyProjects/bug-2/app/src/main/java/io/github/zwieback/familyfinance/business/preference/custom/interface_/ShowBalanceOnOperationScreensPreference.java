package io.github.zwieback.familyfinance.business.preference.custom.interface_;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.core.preference.config.InterfacePrefs;
import io.github.zwieback.familyfinance.core.preference.custom.BooleanPreference;

public class ShowBalanceOnOperationScreensPreference extends BooleanPreference {

    @NonNull
    private InterfacePrefs interfacePrefs;

    @SuppressWarnings("unused")
    public ShowBalanceOnOperationScreensPreference(Context context,
                                                   AttributeSet attrs,
                                                   int defStyleAttr,
                                                   int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public ShowBalanceOnOperationScreensPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public ShowBalanceOnOperationScreensPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public ShowBalanceOnOperationScreensPreference(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        interfacePrefs = InterfacePrefs.with(context);
        callChangeListener(isShowBalance());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setShowBalance((boolean) newValue);
        return true;
    }

    private boolean isShowBalance() {
        return interfacePrefs.isShowBalanceOnOperationScreens();
    }

    private void setShowBalance(boolean showBalance) {
        interfacePrefs.setShowBalanceOnOperationScreens(showBalance);
    }
}
