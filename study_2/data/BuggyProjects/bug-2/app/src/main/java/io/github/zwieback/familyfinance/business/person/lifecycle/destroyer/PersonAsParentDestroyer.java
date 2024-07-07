package io.github.zwieback.familyfinance.business.person.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityAsParentDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Person;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.reactivex.ReactiveEntityStore;

public class PersonAsParentDestroyer extends EntityAsParentDestroyer<Person> {

    public PersonAsParentDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Person> next() {
        return new PersonFromAccountsDestroyer(context, data);
    }

    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }

    @Override
    protected QueryExpression<Integer> getParentIdExpression() {
        return Person.PARENT_ID;
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.persons_with_parent_exists;
    }
}
