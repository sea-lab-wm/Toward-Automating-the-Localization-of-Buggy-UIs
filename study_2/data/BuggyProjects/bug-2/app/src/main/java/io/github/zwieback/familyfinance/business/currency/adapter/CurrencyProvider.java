package io.github.zwieback.familyfinance.business.currency.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Currency;

public class CurrencyProvider extends EntityProvider<Currency> {

    public CurrencyProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(Currency currency) {
        return CommunityMaterial.Icon.cmd_currency_sign;
    }

    @Override
    public int provideDefaultIconColor(Currency currency) {
        return R.color.colorDollar;
    }
}
