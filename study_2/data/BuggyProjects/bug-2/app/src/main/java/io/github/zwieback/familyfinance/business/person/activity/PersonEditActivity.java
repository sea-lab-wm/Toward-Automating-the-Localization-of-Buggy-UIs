package io.github.zwieback.familyfinance.business.person.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Arrays;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.person.adapter.PersonProvider;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.databinding.ActivityEditPersonBinding;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.PERSON_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.nonNullId;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToInt;

public class PersonEditActivity
        extends EntityFolderEditActivity<Person, ActivityEditPersonBinding> {

    public static final String INPUT_PERSON_ID = "personId";
    public static final String OUTPUT_PERSON_ID = "resultPersonId";

    @Override
    protected int getTitleStringId() {
        return R.string.person_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_person;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_PERSON_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_PERSON_ID;
    }

    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }

    @Override
    protected EntityProvider<Person> createProvider() {
        return new PersonProvider(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PERSON_CODE:
                int parentId = extractOutputId(resultIntent, RESULT_PERSON_ID);
                loadParent(parentId);
                break;
        }
    }

    public void onParentClick(View view) {
        Intent intent = new Intent(this, PersonActivity.class);
        intent.putExtra(EntityActivity.INPUT_REGULAR_SELECTABLE, false);
        intent.putExtra(EntityFolderActivity.INPUT_PROHIBITED_FOLDER_ID, entity.getId());
        startActivityForResult(intent, PERSON_CODE);
    }

    private void onParentRemoved() {
        entity.setParent(null);
        binding.parentLayout.setError(null);
    }

    private void loadParent(int parentId) {
        if (nonNullId(parentId)) {
            loadEntity(Person.class, parentId, foundPerson -> {
                entity.setParent(foundPerson);
                binding.parentLayout.setError(null);
            });
        }
    }

    @Override
    protected void createEntity() {
        int parentId = extractInputId(INPUT_PARENT_ID);
        boolean folder = extractInputBoolean(INPUT_IS_FOLDER);
        Person person = new Person();
        person.setFolder(folder);
        bind(person);
        loadParent(parentId);
        disableLayout(binding.parentLayout, R.string.hint_parent_disabled);
    }

    @Override
    protected void bind(Person person) {
        entity = person;
        binding.setPerson(person);
        provider.setupIcon(binding.icon.getIcon(), person);
        super.bind(person);
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.parent.setOnClickListener(this::onParentClick);
        binding.parent.setOnClearTextListener(this::onParentRemoved);
        binding.parentLayout.setValidator(this::isParentValid);
    }

    @Override
    protected void updateEntityProperties(Person person) {
        person.setName(binding.name.getText().toString());
        person.setOrderCode(stringToInt(binding.orderCode.getText().toString()));
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        return Arrays.asList(binding.parentLayout, binding.nameLayout, binding.orderCodeLayout);
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }

    private boolean isParentValid(String input) {
        return isParentValid(input, (Person) entity.getParent(), Person.$TYPE.getName());
    }

    @Override
    protected ValidatingTextInputLayout getParentLayout() {
        return binding.parentLayout;
    }

    @Override
    protected boolean isParentInsideItself(int parentId, int newParentId) {
        return isParentInsideItself(newParentId, Person.ID,
                Person.PARENT_ID.eq(parentId).and(Person.FOLDER.eq(true)),
                this::isParentInsideItself);
    }
}
