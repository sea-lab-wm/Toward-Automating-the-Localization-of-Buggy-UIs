package io.github.zwieback.familyfinance.core.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityDestroyer<E extends IBaseEntity> {

    protected final Context context;
    protected final ReactiveEntityStore<Persistable> data;

    public EntityDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        this.context = context;
        this.data = data;
    }

    protected abstract EntityDestroyer<E> next();

    public abstract void destroy(E entity, Consumer<Integer> terminalConsumer);
}
