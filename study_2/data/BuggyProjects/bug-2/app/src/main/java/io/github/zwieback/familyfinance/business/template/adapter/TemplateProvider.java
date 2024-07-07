package io.github.zwieback.familyfinance.business.template.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.typeface.IIcon;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.template.exception.UnsupportedTemplateTypeException;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Template;

public class TemplateProvider extends EntityProvider<Template> {

    public TemplateProvider(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public IIcon provideDefaultIcon(Template template) {
        return FontAwesome.Icon.faw_file_alt2;
    }

    @Override
    public int provideDefaultIconColor(Template template) {
        switch (template.getType()) {
            case EXPENSE_OPERATION:
                return R.color.colorExpense;
            case INCOME_OPERATION:
                return R.color.colorIncome;
            case TRANSFER_OPERATION:
                return R.color.colorPrimaryDark;
            default:
                throw new UnsupportedTemplateTypeException(template.getId(), template.getType());
        }
    }

    @Override
    public int provideTextColor(Template template) {
        return ContextCompat.getColor(context, provideDefaultIconColor(template));
    }
}
