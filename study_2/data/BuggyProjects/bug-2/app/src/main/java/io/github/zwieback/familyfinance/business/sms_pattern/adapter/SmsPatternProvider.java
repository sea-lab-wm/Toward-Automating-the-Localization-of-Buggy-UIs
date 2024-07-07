package io.github.zwieback.familyfinance.business.sms_pattern.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.SmsPattern;

public class SmsPatternProvider extends EntityProvider<SmsPattern> {

    public SmsPatternProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(SmsPattern smsPattern) {
        if (smsPattern.isCommon()) {
            return FontAwesome.Icon.faw_comment;
        }
        return FontAwesome.Icon.faw_comment2;
    }

    @Override
    public int provideDefaultIconColor(SmsPattern smsPattern) {
        return R.color.colorPrimaryDark;
    }

    @Override
    public int provideTextColor(SmsPattern smsPattern) {
        return ContextCompat.getColor(context, provideDefaultIconColor(smsPattern));
    }
}
