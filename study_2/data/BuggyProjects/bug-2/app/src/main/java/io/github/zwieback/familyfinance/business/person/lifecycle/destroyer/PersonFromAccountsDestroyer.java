package io.github.zwieback.familyfinance.business.person.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Person;
import io.requery.Persistable;
import io.requery.query.Condition;
import io.requery.reactivex.ReactiveEntityStore;

public class PersonFromAccountsDestroyer extends EntityFromDestroyer<Person, Account> {

    public PersonFromAccountsDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Person> next() {
        return new PersonFromExpenseOperationsDestroyer(context, data);
    }

    @Override
    protected Class<Account> getFromClass() {
        return Account.class;
    }

    @Override
    protected Condition<?, ?> getWhereCondition(Person person) {
        return Account.OWNER_ID.eq(person.getId());
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.accounts_with_owner_exists;
    }
}
