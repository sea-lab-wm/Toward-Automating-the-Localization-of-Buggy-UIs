package io.github.zwieback.familyfinance.core.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.view.View;

import io.github.zwieback.familyfinance.core.filter.EntityFolderFilter;
import io.github.zwieback.familyfinance.core.listener.EntityFolderClickListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntityFolder;
import io.requery.Persistable;
import io.requery.meta.Type;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityFolderAdapter<
        ENTITY extends IBaseEntityFolder,
        FILTER extends EntityFolderFilter,
        BINDING extends ViewDataBinding,
        LISTENER extends EntityFolderClickListener<ENTITY>>
        extends EntityAdapter<ENTITY, FILTER, BINDING, LISTENER> {

    @Nullable
    protected final Integer parentId;

    protected EntityFolderAdapter(Type<ENTITY> type,
                                  Context context,
                                  LISTENER clickListener,
                                  ReactiveEntityStore<Persistable> data,
                                  FILTER filter) {
        super(type, context, clickListener, data, filter);
        this.parentId = filter.getParentId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View view) {
        BINDING binding = (BINDING) view.getTag();
        if (binding == null) {
            return;
        }
        ENTITY entity = extractEntity(binding);
        if (entity.isFolder()) {
            clickListener.onFolderClick(view, entity);
        } else {
            clickListener.onEntityClick(view, entity);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onLongClick(View view) {
        BINDING binding = (BINDING) view.getTag();
        if (binding == null) {
            return false;
        }
        ENTITY entity = extractEntity(binding);
        if (entity.isFolder()) {
            clickListener.onFolderLongClick(view, entity);
        } else {
            clickListener.onEntityLongClick(view, entity);
        }
        return true;
    }
}
