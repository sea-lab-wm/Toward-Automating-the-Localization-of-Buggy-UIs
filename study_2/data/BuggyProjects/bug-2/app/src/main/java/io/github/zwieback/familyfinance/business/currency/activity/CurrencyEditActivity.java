package io.github.zwieback.familyfinance.business.currency.activity;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Arrays;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.currency.adapter.CurrencyProvider;
import io.github.zwieback.familyfinance.core.activity.EntityEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.databinding.ActivityEditCurrencyBinding;

public class CurrencyEditActivity
        extends EntityEditActivity<Currency, ActivityEditCurrencyBinding> {

    public static final String INPUT_CURRENCY_ID = "currencyId";
    public static final String OUTPUT_CURRENCY_ID = "resultCurrencyId";

    @Override
    protected int getTitleStringId() {
        return R.string.currency_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_currency;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_CURRENCY_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_CURRENCY_ID;
    }

    @Override
    protected Class<Currency> getEntityClass() {
        return Currency.class;
    }

    @Override
    protected EntityProvider<Currency> createProvider() {
        return new CurrencyProvider(this);
    }

    @Override
    protected void createEntity() {
        bind(new Currency());
    }

    @Override
    protected void bind(Currency currency) {
        entity = currency;
        binding.setCurrency(currency);
        provider.setupIcon(binding.icon.getIcon(), currency);
        super.bind(currency);
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
    }

    @Override
    protected void updateEntityProperties(Currency currency) {
        currency.setName(binding.name.getText().toString());
        currency.setDescription(binding.description.getText().toString());
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        return Arrays.asList(binding.nameLayout, binding.descriptionLayout);
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }
}
