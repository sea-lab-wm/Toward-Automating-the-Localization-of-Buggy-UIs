package io.github.zwieback.familyfinance.core.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityFromPreferencesDestroyer<E extends IBaseEntity>
        extends EntityAlertDestroyer<E> {

    protected DatabasePrefs databasePrefs;

    public EntityFromPreferencesDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
        databasePrefs = DatabasePrefs.with(context);
    }

    protected abstract boolean preferencesContainsEntity(E entity);

    @Override
    public void destroy(E entity, Consumer<Integer> terminalConsumer) {
        if (preferencesContainsEntity(entity)) {
            showAlert(getAlertResourceId());
        } else {
            next().destroy(entity, terminalConsumer);
        }
    }
}
