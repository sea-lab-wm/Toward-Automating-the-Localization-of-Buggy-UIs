package io.github.zwieback.familyfinance.core.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import io.github.zwieback.familyfinance.core.listener.EntityClickListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.requery.Persistable;
import io.requery.android.QueryRecyclerAdapter;
import io.requery.meta.Type;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityAdapter<
        ENTITY extends IBaseEntity,
        FILTER extends EntityFilter,
        BINDING extends ViewDataBinding,
        LISTENER extends EntityClickListener<ENTITY>>
        extends QueryRecyclerAdapter<ENTITY, BindingHolder<BINDING>>
        implements View.OnClickListener, View.OnLongClickListener {

    protected final Context context;
    protected final ReactiveEntityStore<Persistable> data;
    protected final EntityProvider<ENTITY> provider;
    protected final LISTENER clickListener;
    protected FILTER filter;

    protected EntityAdapter(Type<ENTITY> type,
                            Context context,
                            LISTENER clickListener,
                            ReactiveEntityStore<Persistable> data,
                            FILTER filter) {
        super(type);
        this.context = context;
        this.data = data;
        this.provider = createProvider(context);
        this.clickListener = clickListener;
        this.filter = filter;
    }

    protected abstract EntityProvider<ENTITY> createProvider(Context context);

    protected abstract BINDING inflate(LayoutInflater inflater);

    protected abstract ENTITY extractEntity(BINDING binding);

    @Override
    public BindingHolder<BINDING> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BINDING binding = inflate(inflater);
        binding.getRoot().setTag(binding);
        binding.getRoot().setOnClickListener(this);
        binding.getRoot().setOnLongClickListener(this);
        return new BindingHolder<>(binding);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onClick(View view) {
        BINDING binding = (BINDING) view.getTag();
        if (binding == null) {
            return;
        }
        clickListener.onEntityClick(view, extractEntity(binding));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onLongClick(View view) {
        BINDING binding = (BINDING) view.getTag();
        if (binding == null) {
            return false;
        }
        clickListener.onEntityLongClick(view, extractEntity(binding));
        return true;
    }

    public final void applyFilter(FILTER filter) {
        this.filter = filter;
    }
}
