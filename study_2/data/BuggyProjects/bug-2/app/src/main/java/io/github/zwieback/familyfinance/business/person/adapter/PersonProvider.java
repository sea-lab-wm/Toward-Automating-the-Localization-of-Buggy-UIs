package io.github.zwieback.familyfinance.business.person.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Person;

public class PersonProvider extends EntityProvider<Person> {

    public PersonProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(Person person) {
        return person.isFolder()
                ? CommunityMaterial.Icon.cmd_folder_account
                : CommunityMaterial.Icon.cmd_account_circle;
    }

    @Override
    public int provideDefaultIconColor(Person person) {
        return R.color.colorPrimaryDark;
    }
}
