package io.github.zwieback.familyfinance.business.person.lifecycle.destroyer;

import android.content.Context;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityFromPreferencesDestroyer;
import io.github.zwieback.familyfinance.core.model.Person;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

class PersonFromPreferencesDestroyer extends EntityFromPreferencesDestroyer<Person> {

    PersonFromPreferencesDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    protected EntityDestroyer<Person> next() {
        return new PersonForceDestroyer(context, data);
    }

    @Override
    protected int getAlertResourceId() {
        return R.string.preferences_contains_person;
    }

    @Override
    protected boolean preferencesContainsEntity(Person person) {
        int personId = databasePrefs.getPersonId();
        return personId == person.getId();
    }
}
