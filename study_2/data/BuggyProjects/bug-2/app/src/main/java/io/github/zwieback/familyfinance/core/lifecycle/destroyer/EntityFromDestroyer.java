package io.github.zwieback.familyfinance.core.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityFromDestroyer<E extends IBaseEntity, F extends IBaseEntity>
        extends EntityAlertDestroyer<E> {

    public EntityFromDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    protected abstract Class<F> getFromClass();

    protected abstract Condition<?, ?> getWhereCondition(E entity);

    @Override
    public void destroy(E entity, Consumer<Integer> terminalConsumer) {
        data.count(getFromClass())
                .where(getWhereCondition(entity)).get().single()
                .subscribe(operationCount -> {
                    if (operationCount > 0) {
                        showAlert(getAlertResourceId());
                    } else {
                        next().destroy(entity, terminalConsumer);
                    }
                });
    }
}
