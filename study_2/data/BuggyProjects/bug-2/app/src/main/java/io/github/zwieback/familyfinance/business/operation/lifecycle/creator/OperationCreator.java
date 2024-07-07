package io.github.zwieback.familyfinance.business.operation.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.TreeSet;

import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class OperationCreator extends EntityCreator<Operation> {

    public OperationCreator(@NonNull Context context,
                            @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Operation> buildEntities() {
        return new TreeSet<>(this);
    }

    @Override
    public int compare(Operation left, Operation right) {
        return left.getDate().compareTo(right.getDate());
    }
}
