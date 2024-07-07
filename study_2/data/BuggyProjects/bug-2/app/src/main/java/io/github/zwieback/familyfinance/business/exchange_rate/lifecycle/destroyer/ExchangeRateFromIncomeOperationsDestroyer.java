package io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

class ExchangeRateFromIncomeOperationsDestroyer
        extends EntityFromDestroyer<ExchangeRate, Operation> {

    ExchangeRateFromIncomeOperationsDestroyer(Context context,
                                              ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<ExchangeRate> next() {
        return new ExchangeRateFromTransferOperationsDestroyer(context, data);
    }

    @Override
    protected Class<Operation> getFromClass() {
        return Operation.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(ExchangeRate exchangeRate) {
        return Operation.TYPE.eq(OperationType.INCOME_OPERATION)
                .and(Operation.EXCHANGE_RATE_ID.eq(exchangeRate.getId()));
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.income_operations_with_exchange_rate_exists;
    }
}
