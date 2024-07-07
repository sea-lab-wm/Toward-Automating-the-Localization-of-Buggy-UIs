package io.github.zwieback.familyfinance.business.person.activity;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.person.filter.PersonFilter;
import io.github.zwieback.familyfinance.business.person.fragment.PersonFragment;
import io.github.zwieback.familyfinance.business.person.lifecycle.destroyer.PersonAsParentDestroyer;
import io.github.zwieback.familyfinance.business.person.lifecycle.destroyer.PersonFromAccountsDestroyer;
import io.github.zwieback.familyfinance.business.person.listener.OnPersonClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.PersonView;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;
import static io.github.zwieback.familyfinance.business.person.filter.PersonFilter.PERSON_FILTER;
import static io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity.INPUT_IS_FOLDER;
import static io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity.INPUT_PARENT_ID;

public class PersonActivity
        extends EntityFolderActivity<PersonView, Person, PersonFilter, PersonFragment>
        implements OnPersonClickListener {

    @Override
    protected int getTitleStringId() {
        return R.string.person_activity_title;
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return PERSON_FILTER;
    }

    @NonNull
    @Override
    protected PersonFilter createDefaultFilter() {
        return new PersonFilter();
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_PERSON_ID;
    }

    @Override
    protected String getFragmentTag() {
        return String.format("%s_%s", getLocalClassName(), filter.getParentId());
    }

    @Override
    protected PersonFragment createFragment() {
        return PersonFragment.newInstance(filter);
    }

    @Override
    protected void addEntity(int parentId, boolean isFolder) {
        super.addEntity(parentId, isFolder);
        Intent intent = new Intent(this, PersonEditActivity.class);
        intent.putExtra(INPUT_PARENT_ID, parentId);
        intent.putExtra(INPUT_IS_FOLDER, isFolder);
        startActivity(intent);
    }

    @Override
    protected void editEntity(PersonView person) {
        super.editEntity(person);
        Intent intent = new Intent(this, PersonEditActivity.class);
        intent.putExtra(PersonEditActivity.INPUT_PERSON_ID, person.getId());
        startActivity(intent);
    }

    @Override
    protected Class<Person> getClassOfRegularEntity() {
        return Person.class;
    }

    @Override
    protected EntityDestroyer<Person> createDestroyer(PersonView person) {
        if (person.isFolder()) {
            return new PersonAsParentDestroyer(this, data);
        }
        return new PersonFromAccountsDestroyer(this, data);
    }
}
