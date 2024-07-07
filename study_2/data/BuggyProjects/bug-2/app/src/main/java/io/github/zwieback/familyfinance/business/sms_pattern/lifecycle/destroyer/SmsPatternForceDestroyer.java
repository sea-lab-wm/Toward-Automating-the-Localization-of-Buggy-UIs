package io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

public class SmsPatternForceDestroyer extends EntityForceDestroyer<SmsPattern> {

    public SmsPatternForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<SmsPattern> getEntityClass() {
        return SmsPattern.class;
    }

    @Override
    protected QueryAttribute<SmsPattern, Integer> getIdAttribute() {
        return SmsPattern.ID;
    }
}
