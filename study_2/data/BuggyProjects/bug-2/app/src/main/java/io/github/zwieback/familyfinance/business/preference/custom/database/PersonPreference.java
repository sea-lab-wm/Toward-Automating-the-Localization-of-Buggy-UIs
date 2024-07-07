package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.person.activity.PersonActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.preference.custom.EntityActivityResultPreference;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.PERSON_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;

public class PersonPreference extends EntityActivityResultPreference<Person> {

    @SuppressWarnings("unused")
    public PersonPreference(Context context, AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public PersonPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public PersonPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public PersonPreference(Context context) {
        super(context);
    }

    @Override
    protected int getRequestCode() {
        return PERSON_CODE;
    }

    @Override
    protected Intent getRequestIntent() {
        return new Intent(getContext(), PersonActivity.class)
                .putExtra(EntityFolderActivity.INPUT_FOLDER_SELECTABLE, false);
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_PERSON_ID;
    }

    @Override
    protected int getSavedEntityId() {
        return databasePrefs.getPersonId();
    }

    @Override
    protected void saveEntityId(int personId) {
        databasePrefs.setPersonId(personId);
    }

    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }

    @Override
    protected String getEntityName(@NonNull Person person) {
        return person.getName();
    }

    @Override
    protected int getPreferenceTitleRes() {
        return R.string.person_id_preference_title;
    }
}
