package io.github.zwieback.familyfinance.business.person.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import io.github.zwieback.familyfinance.business.person.filter.PersonFilter;
import io.github.zwieback.familyfinance.business.person.listener.OnPersonClickListener;
import io.github.zwieback.familyfinance.business.person.query.PersonQueryBuilder;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityFolderAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.PersonView;
import io.github.zwieback.familyfinance.databinding.ItemPersonBinding;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class PersonAdapter extends EntityFolderAdapter<PersonView, PersonFilter, ItemPersonBinding,
        OnPersonClickListener> {

    public PersonAdapter(Context context,
                         OnPersonClickListener clickListener,
                         ReactiveEntityStore<Persistable> data,
                         PersonFilter filter) {
        super(PersonView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<PersonView> createProvider(Context context) {
        return new PersonViewProvider(context);
    }

    @Override
    protected ItemPersonBinding inflate(LayoutInflater inflater) {
        return ItemPersonBinding.inflate(inflater);
    }

    @Override
    protected PersonView extractEntity(ItemPersonBinding binding) {
        return (PersonView) binding.getPerson();
    }

    @Override
    public Result<PersonView> performQuery() {
        return PersonQueryBuilder.create(data)
                .setParentId(parentId)
                .build();
    }

    @Override
    public void onBindViewHolder(PersonView person, BindingHolder<ItemPersonBinding> holder,
                                 int position) {
        holder.binding.setPerson(person);
        provider.setupIcon(holder.binding.icon.getIcon(), person);
    }
}
