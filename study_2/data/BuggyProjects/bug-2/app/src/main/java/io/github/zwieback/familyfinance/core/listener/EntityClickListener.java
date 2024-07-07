package io.github.zwieback.familyfinance.core.listener;

import android.view.View;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;

public interface EntityClickListener<E extends IBaseEntity> {

    void onEntityClick(View view, E entity);

    void onEntityLongClick(View view, E entity);
}
