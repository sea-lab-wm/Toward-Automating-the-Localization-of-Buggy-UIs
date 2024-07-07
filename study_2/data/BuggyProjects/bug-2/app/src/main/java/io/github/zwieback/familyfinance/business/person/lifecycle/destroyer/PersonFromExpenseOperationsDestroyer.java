package io.github.zwieback.familyfinance.business.person.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

class PersonFromExpenseOperationsDestroyer extends EntityFromDestroyer<Person, Operation> {

    PersonFromExpenseOperationsDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Person> next() {
        return new PersonFromIncomeOperationsDestroyer(context, data);
    }

    @Override
    protected Class<Operation> getFromClass() {
        return Operation.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Person person) {
        return Operation.TYPE.eq(OperationType.EXPENSE_OPERATION)
                .and(Operation.OWNER_ID.eq(person.getId()));
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.expense_operations_with_owner_exists;
    }
}
