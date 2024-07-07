package io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.TreeSet;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class SmsPatternCreator extends EntityCreator<SmsPattern> {

    public SmsPatternCreator(@NonNull Context context,
                             @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<SmsPattern> buildEntities() {
        return new TreeSet<>(this);
    }

    @Override
    public int compare(SmsPattern left, SmsPattern right) {
        return Integer.compare(left.getId(), right.getId());
    }
}
