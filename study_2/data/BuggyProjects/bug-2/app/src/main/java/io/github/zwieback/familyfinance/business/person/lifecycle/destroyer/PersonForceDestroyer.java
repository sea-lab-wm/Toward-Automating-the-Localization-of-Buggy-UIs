package io.github.zwieback.familyfinance.business.person.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityForceDestroyer;
import io.github.zwieback.familyfinance.core.model.Person;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.reactivex.ReactiveEntityStore;

class PersonForceDestroyer extends EntityForceDestroyer<Person> {

    PersonForceDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }

    @Override
    protected QueryAttribute<Person, Integer> getIdAttribute() {
        return Person.ID;
    }
}
