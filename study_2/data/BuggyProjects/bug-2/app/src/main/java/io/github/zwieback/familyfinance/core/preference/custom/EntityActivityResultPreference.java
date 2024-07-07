package io.github.zwieback.familyfinance.core.preference.custom;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.util.StringUtils;

import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;

public abstract class EntityActivityResultPreference<E extends IBaseEntity>
        extends ActivityResultPreference {

    public EntityActivityResultPreference(Context context, AttributeSet attrs, int defStyleAttr,
                                          int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EntityActivityResultPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EntityActivityResultPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EntityActivityResultPreference(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        callChangeListener(getSavedEntityId());
    }

    @Override
    public void onSuccessResult(@NonNull Intent resultIntent) {
        int entityId = extractOutputId(resultIntent, getResultName());
        saveEntityId(entityId);
        callChangeListener(entityId);
    }

    @NonNull
    protected abstract String getResultName();

    protected abstract int getSavedEntityId();

    protected abstract void saveEntityId(int entityId);

    protected abstract Class<E> getEntityClass();

    protected abstract String getEntityName(@NonNull E entity);

    @StringRes
    protected abstract int getPreferenceTitleRes();

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int newEntityId = (int) newValue;
        activity.loadEntity(getEntityClass(), newEntityId,
                foundEntity -> {
                    String entityTitle = activity.getString(getPreferenceTitleRes(),
                            getEntityName(foundEntity));
                    preference.setTitle(entityTitle);
                },
                throwable -> {
                    String entityTitle = activity.getString(getPreferenceTitleRes(),
                            StringUtils.UNDEFINED);
                    preference.setTitle(entityTitle);
                });
        return true;
    }

    private static int extractOutputId(Intent resultIntent, String name) {
        return resultIntent.getIntExtra(name, ID_AS_NULL);
    }
}
