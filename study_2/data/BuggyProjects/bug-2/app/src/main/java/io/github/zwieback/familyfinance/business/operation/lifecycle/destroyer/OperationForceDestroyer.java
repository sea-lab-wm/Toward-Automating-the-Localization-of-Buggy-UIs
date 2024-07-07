package io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

public class OperationForceDestroyer extends EntityForceDestroyer<Operation> {

    public OperationForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<Operation> getEntityClass() {
        return Operation.class;
    }

    @Override
    protected QueryAttribute<Operation, Integer> getIdAttribute() {
        return Operation.ID;
    }
}
