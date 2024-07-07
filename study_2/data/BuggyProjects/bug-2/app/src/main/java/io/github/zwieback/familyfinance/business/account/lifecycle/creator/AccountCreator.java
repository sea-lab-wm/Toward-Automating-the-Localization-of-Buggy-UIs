package io.github.zwieback.familyfinance.business.account.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.type.AccountType;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class AccountCreator extends EntityCreator<Account> {

    public AccountCreator(@NonNull Context context,
                          @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Account> buildEntities() {
        Currency defaultCurrency = findCurrency(databasePrefs.getCurrencyId());
        Person chief = findPerson(databasePrefs.getPersonId());

        Set<Account> accounts = new TreeSet<>(this);
        Account cash = createAccount(null, defaultCurrency, chief, getString(R.string.account_cash),
                false, BigDecimal.ZERO, 1, AccountType.CASH_ACCOUNT);
        accounts.add(cash);
        return accounts;
    }

    @Override
    public int compare(Account left, Account right) {
        return Integer.compare(left.getOrderCode(), right.getOrderCode());
    }

    @NonNull
    private Person findPerson(int personId) {
        ReactiveResult<Person> persons = data
                .select(Person.class)
                .where(Person.ID.eq(personId))
                .get();
        return persons.first();
    }

    @NonNull
    private Currency findCurrency(int currencyId) {
        ReactiveResult<Currency> currencies = data
                .select(Currency.class)
                .where(Currency.ID.eq(currencyId))
                .get();
        return currencies.first();
    }

    @NonNull
    private static Account createAccount(@Nullable Account parent,
                                         @Nullable Currency currency,
                                         @Nullable Person owner,
                                         @NonNull String name,
                                         boolean folder,
                                         @Nullable BigDecimal initialBalance,
                                         int orderCode,
                                         @NonNull AccountType type) {
        return new Account()
                .setActive(true)
                .setParent(parent)
                .setCurrency(currency)
                .setOwner(owner)
                .setName(name)
                .setFolder(folder)
                .setInitialBalance(initialBalance)
                .setOrderCode(orderCode)
                .setType(type);
    }
}
