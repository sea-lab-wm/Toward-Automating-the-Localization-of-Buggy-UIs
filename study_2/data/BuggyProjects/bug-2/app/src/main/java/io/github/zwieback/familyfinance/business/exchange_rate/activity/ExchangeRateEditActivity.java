package io.github.zwieback.familyfinance.business.exchange_rate.activity;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import org.threeten.bp.LocalDate;

import java.util.Arrays;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.adapter.ExchangeRateProvider;
import io.github.zwieback.familyfinance.core.activity.EntityEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.databinding.ActivityEditExchangeRateBinding;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.util.DateUtils.calendarDateToLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.isTextAnLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.localDateToString;
import static io.github.zwieback.familyfinance.util.DateUtils.now;
import static io.github.zwieback.familyfinance.util.DateUtils.stringToLocalDate;
import static io.github.zwieback.familyfinance.util.DialogUtils.showDatePickerDialog;
import static io.github.zwieback.familyfinance.util.NumberUtils.nonNullId;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;

public class ExchangeRateEditActivity
        extends EntityEditActivity<ExchangeRate, ActivityEditExchangeRateBinding>
        implements OnDateSetListener {

    public static final String INPUT_EXCHANGE_RATE_ID = "exchangeRateId";
    public static final String INPUT_CURRENCY_ID = "currencyId";
    public static final String OUTPUT_EXCHANGE_RATE_ID = "resultExchangeRateId";

    @Override
    protected int getTitleStringId() {
        return R.string.exchange_rate_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_exchange_rate;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_EXCHANGE_RATE_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_EXCHANGE_RATE_ID;
    }

    @Override
    protected Class<ExchangeRate> getEntityClass() {
        return ExchangeRate.class;
    }

    @Override
    protected EntityProvider<ExchangeRate> createProvider() {
        return new ExchangeRateProvider(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CURRENCY_CODE:
                int currencyId = extractOutputId(resultIntent, RESULT_CURRENCY_ID);
                loadCurrency(currencyId);
                break;
        }
    }

    public void onCurrencyClick(View view) {
        Intent intent = new Intent(this, CurrencyActivity.class);
        startActivityForResult(intent, CURRENCY_CODE);
    }

    public void onDateClick(View view) {
        LocalDate date = determineDate();
        showDatePickerDialog(getSupportFragmentManager(), date);
    }

    /**
     * Don't check for {@code null} because the check was completed in {@link #isCorrectDate()}.
     */
    @SuppressWarnings("ConstantConditions")
    @NonNull
    protected LocalDate determineDate() {
        if (isCorrectDate()) {
            return stringToLocalDate(binding.date.getText().toString());
        }
        return entity.getDate();
    }

    private boolean isCorrectDate() {
        return isTextAnLocalDate(binding.date.getText().toString());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        LocalDate date = calendarDateToLocalDate(year, month, day);
        binding.date.setText(localDateToString(date));
    }

    private void loadCurrency(int currencyId) {
        loadEntity(Currency.class, currencyId, foundCurrency -> entity.setCurrency(foundCurrency));
    }

    @Override
    protected void createEntity() {
        int currencyId = extractInputId(INPUT_CURRENCY_ID);
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setDate(now());
        bind(exchangeRate);
        if (nonNullId(currencyId)) {
            loadCurrency(currencyId);
        }
    }

    @Override
    protected void bind(ExchangeRate exchangeRate) {
        entity = exchangeRate;
        binding.setExchangeRate(exchangeRate);
        provider.setupIcon(binding.icon.getIcon(), exchangeRate);
        super.bind(exchangeRate);
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.currency.setOnClickListener(this::onCurrencyClick);
        binding.currency.setOnClearTextListener(() -> entity.setCurrency(null));
        binding.date.setOnClickListener(this::onDateClick);
    }

    @Override
    protected void updateEntityProperties(ExchangeRate exchangeRate) {
        exchangeRate.setValue(stringToBigDecimal(binding.value.getText().toString()));
        exchangeRate.setDate(stringToLocalDate(binding.date.getText().toString()));
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        return Arrays.asList(binding.currencyLayout, binding.valueLayout, binding.dateLayout);
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }
}
