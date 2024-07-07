package io.github.zwieback.familyfinance.core.activity;

import android.os.Bundle;

import io.github.zwieback.familyfinance.app.FamilyFinanceApplication;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class DataActivityWrapper extends ActivityWrapper {

    protected ReactiveEntityStore<Persistable> data;
    protected DatabasePrefs databasePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = ((FamilyFinanceApplication) getApplication()).getData();
        databasePrefs = DatabasePrefs.with(this);
    }

    protected <E extends IBaseEntity> void loadEntity(Class<E> entityClass,
                                                      int entityId,
                                                      Consumer<E> onSuccess,
                                                      Consumer<? super Throwable> onError) {
        data.findByKey(entityClass, entityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError);
    }
}
