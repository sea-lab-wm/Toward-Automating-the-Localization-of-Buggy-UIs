package io.github.zwieback.familyfinance.business.account.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Account;

public class AccountProvider extends EntityProvider<Account> {

    public AccountProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(Account account) {
        if (account.isFolder()) {
            return account.isActive()
                    ? CommunityMaterial.Icon.cmd_folder
                    : CommunityMaterial.Icon.cmd_folder_remove;
        }
        return CommunityMaterial.Icon.cmd_wallet;
    }

    @Override
    public int provideDefaultIconColor(Account account) {
        return R.color.colorPrimaryDark;
    }
}
