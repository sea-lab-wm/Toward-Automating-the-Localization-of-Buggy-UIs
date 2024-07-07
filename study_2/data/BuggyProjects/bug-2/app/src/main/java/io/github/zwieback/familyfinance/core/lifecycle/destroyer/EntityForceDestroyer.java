package io.github.zwieback.familyfinance.core.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityForceDestroyer<E extends IBaseEntity> extends EntityDestroyer<E> {

    public EntityForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<E> next() {
        return null;
    }

    protected abstract Class<E> getEntityClass();

    protected abstract QueryAttribute<E, Integer> getIdAttribute();

    @Override
    public void destroy(E entity, Consumer<Integer> terminalConsumer) {
        data.delete(getEntityClass())
                .where(getIdAttribute().eq(entity.getId())).get().single()
                .subscribe(terminalConsumer);
    }
}
