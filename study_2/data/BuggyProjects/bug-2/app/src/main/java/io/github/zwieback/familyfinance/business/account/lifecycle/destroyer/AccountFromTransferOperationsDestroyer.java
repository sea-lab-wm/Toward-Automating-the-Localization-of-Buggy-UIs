package io.github.zwieback.familyfinance.business.account.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

import static io.github.zwieback.familyfinance.core.model.type.OperationType.TRANSFER_EXPENSE_OPERATION;
import static io.github.zwieback.familyfinance.core.model.type.OperationType.TRANSFER_INCOME_OPERATION;

class AccountFromTransferOperationsDestroyer extends EntityFromDestroyer<Account, Operation> {

    AccountFromTransferOperationsDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Account> next() {
        return new AccountFromPreferencesDestroyer(context, data);
    }

    @Override
    protected Class<Operation> getFromClass() {
        return Operation.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Account account) {
        return Operation.TYPE.in(TRANSFER_EXPENSE_OPERATION, TRANSFER_INCOME_OPERATION)
                .and(Operation.ACCOUNT_ID.eq(account.getId()));
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.transfer_operations_with_account_exists;
    }
}
