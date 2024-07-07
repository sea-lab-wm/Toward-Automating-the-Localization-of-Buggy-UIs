package io.github.zwieback.familyfinance.business.person.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;
import java.util.TreeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Person;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class PersonCreator extends EntityCreator<Person> {

    public PersonCreator(@NonNull Context context,
                         @NonNull ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @NonNull
    @Override
    protected Iterable<Person> buildEntities() {
        Set<Person> people = new TreeSet<>(this);
        people.add(createPerson(null, getString(R.string.person_chief), false, 1));
        return people;
    }

    @Override
    public int compare(Person left, Person right) {
        return Integer.valueOf(left.getOrderCode()).compareTo(right.getOrderCode());
    }

    @NonNull
    private static Person createPerson(@Nullable Person parent,
                                       @NonNull String name,
                                       boolean folder,
                                       int orderCode) {
        return new Person()
                .setParent(parent)
                .setName(name)
                .setFolder(folder)
                .setOrderCode(orderCode);
    }
}
