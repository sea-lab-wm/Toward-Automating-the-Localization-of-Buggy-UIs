package io.github.zwieback.familyfinance.business.account.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

public class AccountFromExpenseOperationsDestroyer
        extends EntityFromDestroyer<Account, Operation> {

    public AccountFromExpenseOperationsDestroyer(Context context,
                                                 ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Account> next() {
        return new AccountFromIncomeOperationsDestroyer(context, data);
    }

    @Override
    protected Class<Operation> getFromClass() {
        return Operation.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Account account) {
        return Operation.ACCOUNT_ID.eq(account.getId())
                .and(Operation.TYPE.eq(OperationType.EXPENSE_OPERATION));
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.expense_operations_with_account_exists;
    }
}
