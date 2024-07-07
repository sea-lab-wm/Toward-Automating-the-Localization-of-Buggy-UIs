package io.github.zwieback.familyfinance.business.account.adapter.calculator;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.AccountView;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class AccountBalanceCalculator {

    final ReactiveEntityStore<Persistable> data;
    final AccountView account;

    AccountBalanceCalculator(ReactiveEntityStore<Persistable> data, AccountView account) {
        this.data = data;
        this.account = account;
    }

    public abstract void calculateBalance(Consumer<BigDecimal> showBalanceConsumer);
}
