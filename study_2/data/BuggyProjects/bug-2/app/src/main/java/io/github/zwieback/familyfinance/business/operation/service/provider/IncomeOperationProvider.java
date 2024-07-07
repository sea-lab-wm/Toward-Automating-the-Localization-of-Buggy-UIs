package io.github.zwieback.familyfinance.business.operation.service.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Operation;

public class IncomeOperationProvider extends EntityProvider<Operation> {

    public IncomeOperationProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(Operation operation) {
        return FontAwesome.Icon.faw_plus_circle;
    }

    @Override
    public int provideDefaultIconColor(Operation operation) {
        return R.color.colorIncome;
    }

    @Override
    public int provideTextColor(Operation operation) {
        return ContextCompat.getColor(context, provideDefaultIconColor(operation));
    }
}
