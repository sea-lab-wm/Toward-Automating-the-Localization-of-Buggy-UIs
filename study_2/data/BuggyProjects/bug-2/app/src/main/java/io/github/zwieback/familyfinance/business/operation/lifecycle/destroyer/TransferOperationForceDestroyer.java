package io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

import static io.reactivex.internal.functions.Functions.emptyConsumer;

public class TransferOperationForceDestroyer extends EntityForceDestroyer<Operation> {

    public TransferOperationForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
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

    @Override
    public void destroy(Operation operation, Consumer<Integer> terminalConsumer) {
        super.destroy((Operation) operation.getLinkedTransferOperation(), emptyConsumer());
        super.destroy(operation, terminalConsumer);
    }
}
