package io.github.zwieback.familyfinance.business.person.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.person.adapter.PersonAdapter;
import io.github.zwieback.familyfinance.business.person.filter.PersonFilter;
import io.github.zwieback.familyfinance.business.person.listener.OnPersonClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFolderFragment;
import io.github.zwieback.familyfinance.core.model.PersonView;
import io.github.zwieback.familyfinance.databinding.ItemPersonBinding;

import static io.github.zwieback.familyfinance.business.person.filter.PersonFilter.PERSON_FILTER;

public class PersonFragment extends EntityFolderFragment<PersonView, PersonFilter,
        ItemPersonBinding, OnPersonClickListener, PersonAdapter> {

    public static PersonFragment newInstance(PersonFilter filter) {
        PersonFragment fragment = new PersonFragment();
        Bundle args = createArguments(PERSON_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected PersonAdapter createEntityAdapter() {
        PersonFilter filter = extractFilter(PERSON_FILTER);
        return new PersonAdapter(context, clickListener, data, filter);
    }
}
