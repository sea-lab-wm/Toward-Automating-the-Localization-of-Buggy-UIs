package io.github.zwieback.familyfinance.core.listener;

import android.view.View;

import io.github.zwieback.familyfinance.core.model.IBaseEntityFolder;

public interface EntityFolderClickListener<E extends IBaseEntityFolder>
        extends EntityClickListener<E> {

    void onFolderClick(View view, E entity);

    void onFolderLongClick(View view, E entity);
}
