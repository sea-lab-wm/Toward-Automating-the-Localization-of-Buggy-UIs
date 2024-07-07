package io.github.zwieback.familyfinance.business.exchange_rate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;

public class ExchangeRateProvider extends EntityProvider<ExchangeRate> {

    public ExchangeRateProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(ExchangeRate exchangeRate) {
        return CommunityMaterial.Icon.cmd_cash;
    }

    @Override
    public int provideDefaultIconColor(ExchangeRate exchangeRate) {
        return R.color.colorDollar;
    }
}
