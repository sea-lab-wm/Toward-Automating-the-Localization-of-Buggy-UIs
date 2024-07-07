package io.github.zwieback.familyfinance.core.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityAsParentDestroyer<E extends IBaseEntity>
        extends EntityAlertDestroyer<E> {

    public EntityAsParentDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    protected abstract Class<E> getEntityClass();

    protected abstract QueryExpression<Integer> getParentIdExpression();

    @Override
    public void destroy(E entity, Consumer<Integer> terminalConsumer) {
        data.count(getEntityClass())
                .where(getParentIdExpression().eq(entity.getId())).get().single()
                .subscribe(childCount -> {
                    if (childCount > 0) {
                        showAlert(getAlertResourceId());
                    } else {
                        next().destroy(entity, terminalConsumer);
                    }
                });
    }
}
