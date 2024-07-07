package io.github.zwieback.familyfinance.business.person.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.PersonView;

public class PersonViewProvider extends EntityProvider<PersonView> {

    public PersonViewProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(PersonView person) {
        return person.isFolder()
                ? CommunityMaterial.Icon.cmd_folder_account
                : CommunityMaterial.Icon.cmd_account_circle;
    }

    @Override
    public int provideDefaultIconColor(PersonView person) {
        return R.color.colorPrimaryDark;
    }
}
