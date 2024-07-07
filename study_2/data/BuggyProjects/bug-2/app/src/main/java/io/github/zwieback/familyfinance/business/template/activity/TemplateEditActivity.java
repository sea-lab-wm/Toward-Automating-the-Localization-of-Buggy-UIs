package io.github.zwieback.familyfinance.business.template.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.DatePicker;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.activity.AccountActivity;
import io.github.zwieback.familyfinance.business.article.activity.ExpenseArticleActivity;
import io.github.zwieback.familyfinance.business.article.activity.IncomeArticleActivity;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.activity.ExchangeRateActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.helper.ExchangeRateFinder;
import io.github.zwieback.familyfinance.business.person.activity.PersonActivity;
import io.github.zwieback.familyfinance.business.template.adapter.TemplateProvider;
import io.github.zwieback.familyfinance.business.template.exception.UnsupportedTemplateTypeException;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.activity.EntityEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.Template;
import io.github.zwieback.familyfinance.core.model.type.TemplateType;
import io.github.zwieback.familyfinance.databinding.ActivityEditTemplateBinding;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.StringUtils;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ARTICLE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.EXCHANGE_RATE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.INCOME_ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.PERSON_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_EXCHANGE_RATE_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;
import static io.github.zwieback.familyfinance.util.DateUtils.calendarDateToLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.isTextAnLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.localDateToString;
import static io.github.zwieback.familyfinance.util.DateUtils.stringToLocalDate;
import static io.github.zwieback.familyfinance.util.DialogUtils.showDatePickerDialog;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.bigDecimalToString;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;

public class TemplateEditActivity
        extends EntityEditActivity<Template, ActivityEditTemplateBinding>
        implements DatePickerDialog.OnDateSetListener {

    public static final String INPUT_TEMPLATE_ID = "templateId";
    public static final String INPUT_TEMPLATE_TYPE = "templateType";
    public static final String OUTPUT_TEMPLATE_ID = "resultTemplateId";

    @Override
    protected int getTitleStringId() {
        return R.string.template_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_template;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_TEMPLATE_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_TEMPLATE_ID;
    }

    @Override
    protected Class<Template> getEntityClass() {
        return Template.class;
    }

    @Override
    protected EntityProvider<Template> createProvider() {
        return new TemplateProvider(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ARTICLE_CODE:
                int articleId = extractOutputId(resultIntent, RESULT_ARTICLE_ID);
                loadArticle(articleId);
                break;
            case ACCOUNT_CODE:
                int accountId = extractOutputId(resultIntent, RESULT_ACCOUNT_ID);
                loadAccount(accountId);
                break;
            case INCOME_ACCOUNT_CODE:
                int transferAccountId = extractOutputId(resultIntent, RESULT_ACCOUNT_ID);
                loadTransferAccount(transferAccountId);
                break;
            case PERSON_CODE:
                int ownerId = extractOutputId(resultIntent, RESULT_PERSON_ID);
                loadOwner(ownerId);
                break;
            case CURRENCY_CODE:
                int currencyId = extractOutputId(resultIntent, RESULT_CURRENCY_ID);
                loadCurrency(currencyId);
                break;
            case EXCHANGE_RATE_CODE:
                int exchangeRateId = extractOutputId(resultIntent, RESULT_EXCHANGE_RATE_ID);
                loadExchangeRate(exchangeRateId);
                break;
        }
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
    private LocalDate determineDate() {
        if (isCorrectDate()) {
            return stringToLocalDate(binding.date.getText().toString());
        }
        if (entity.getDate() != null) {
            return entity.getDate();
        }
        return DateUtils.now();
    }

    private boolean isCorrectDate() {
        return isTextAnLocalDate(binding.date.getText().toString());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        LocalDate date = calendarDateToLocalDate(year, month, day);
        binding.date.setText(localDateToString(date));
    }

    public void onArticleClick(View view) {
        Intent intent = new Intent(this, determineArticleActivityClass());
        intent.putExtra(EntityActivity.INPUT_READ_ONLY, false);
        startActivityForResult(intent, ARTICLE_CODE);
    }

    private Class<?> determineArticleActivityClass() {
        switch (entity.getType()) {
            case EXPENSE_OPERATION:
                return ExpenseArticleActivity.class;
            case INCOME_OPERATION:
                return IncomeArticleActivity.class;
            default:
                throw new UnsupportedTemplateTypeException(entity.getId(), entity.getType());
        }
    }

    public void onAccountClick(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(AccountActivity.INPUT_ONLY_ACTIVE, true);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    public void onTransferAccountClick(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(AccountActivity.INPUT_ONLY_ACTIVE, true);
        startActivityForResult(intent, INCOME_ACCOUNT_CODE);
    }

    public void onOwnerClick(View view) {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivityForResult(intent, PERSON_CODE);
    }

    public void onCurrencyClick(View view) {
        Intent intent = new Intent(this, CurrencyActivity.class);
        startActivityForResult(intent, CURRENCY_CODE);
    }

    public void onExchangeRateClick(View view) {
        Intent intent = new Intent(this, ExchangeRateActivity.class);
        intent.putExtra(ExchangeRateActivity.INPUT_CURRENCY_ID, determineCurrencyId());
        startActivityForResult(intent, EXCHANGE_RATE_CODE);
    }

    private int determineCurrencyId() {
        if (entity.getExchangeRate() == null) {
            return ID_AS_NULL;
        }
        return entity.getExchangeRate().getCurrency().getId();
    }

    @Nullable
    private ExchangeRate findLastExchangeRate(int currencyId) {
        return new ExchangeRateFinder(data).findLastExchangeRate(currencyId);
    }

    private Consumer<Article> onSuccessfulArticleFound() {
        return foundArticle -> {
            entity.setArticle(foundArticle);
            if (isTextEmpty(binding.value.getText().toString())
                    && foundArticle.getDefaultValue() != null) {
                binding.value.setText(bigDecimalToString(foundArticle.getDefaultValue()));
            }
        };
    }

    private Consumer<Account> onSuccessfulAccountFound() {
        return foundAccount -> {
            entity.setAccount(foundAccount);
            if (foundAccount.getOwner() != null && entity.getOwner() == null) {
                loadOwner(foundAccount.getOwner().getId());
            }
            if (foundAccount.getCurrency() != null && entity.getExchangeRate() == null) {
                loadCurrency(foundAccount.getCurrency().getId());
            }
        };
    }

    private Consumer<Account> onSuccessfulTransferAccountFound() {
        return foundTransferAccount -> entity.setTransferAccount(foundTransferAccount);
    }

    private Consumer<Person> onSuccessfulOwnerFound() {
        return foundOwner -> entity.setOwner(foundOwner);
    }

    private Consumer<Currency> onSuccessfulCurrencyFound() {
        return foundCurrency -> {
            ExchangeRate exchangeRate = findLastExchangeRate(foundCurrency.getId());
            entity.setExchangeRate(exchangeRate);
        };
    }

    private Consumer<ExchangeRate> onSuccessfulExchangeRateFound() {
        return foundExchangeRate -> entity.setExchangeRate(foundExchangeRate);
    }

    private void loadArticle(int articleId) {
        loadEntity(Article.class, articleId, onSuccessfulArticleFound());
    }

    private void loadAccount(int accountId) {
        loadEntity(Account.class, accountId, onSuccessfulAccountFound());
    }

    private void loadTransferAccount(int transferAccountId) {
        loadEntity(Account.class, transferAccountId, onSuccessfulTransferAccountFound());
    }

    private void loadOwner(int ownerId) {
        loadEntity(Person.class, ownerId, onSuccessfulOwnerFound());
    }

    private void loadCurrency(int currencyId) {
        loadEntity(Currency.class, currencyId, onSuccessfulCurrencyFound());
    }

    private void loadExchangeRate(int exchangeRateId) {
        loadEntity(ExchangeRate.class, exchangeRateId, onSuccessfulExchangeRateFound());
    }

    private TemplateType extractType() {
        String type = extractInputString(INPUT_TEMPLATE_TYPE);
        return TemplateType.valueOf(type);
    }

    @Override
    protected void createEntity() {
        Template template = new Template();
        template.setType(extractType());
        bind(template);
    }

    @Override
    protected void bind(Template template) {
        entity = template;
        binding.setTemplate(template);
        binding.value.setTextColor(provider.provideTextColor(template));
        provider.setupIcon(binding.icon.getIcon(), template);
        super.bind(template);
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.articleName.setOnClickListener(this::onArticleClick);
        binding.articleName.setOnClearTextListener(() -> entity.setArticle(null));
        binding.articleCategory.setOnClickListener(this::onArticleClick);
        binding.articleCategory.setOnClearTextListener(() -> entity.setArticle(null));
        binding.account.setOnClickListener(this::onAccountClick);
        binding.account.setOnClearTextListener(() -> entity.setAccount(null));
        binding.transferAccount.setOnClickListener(this::onTransferAccountClick);
        binding.transferAccount.setOnClearTextListener(() -> entity.setTransferAccount(null));
        binding.owner.setOnClickListener(this::onOwnerClick);
        binding.owner.setOnClearTextListener(() -> entity.setOwner(null));
        binding.currency.setOnClickListener(this::onCurrencyClick);
        binding.currency.setOnClearTextListener(() -> entity.setExchangeRate(null));
        binding.exchangeRate.setOnClickListener(this::onExchangeRateClick);
        binding.exchangeRate.setOnClearTextListener(() -> entity.setExchangeRate(null));
        binding.date.setOnClickListener(this::onDateClick);
        binding.date.setOnClearTextListener(() -> entity.setDate(null));

        if (TemplateType.TRANSFER_OPERATION == entity.getType()) {
            loadArticle(databasePrefs.getTransferArticleId());
            disableLayout(binding.articleNameLayout, R.string.hint_article_name_disabled);
            disableLayout(binding.articleCategoryLayout, R.string.hint_article_category_disabled);
        } else {
            disableLayout(binding.transferAccountLayout, R.string.hint_transfer_account_disabled);
        }
    }

    @Override
    protected void updateEntityProperties(Template template) {
        template.setName(binding.name.getText().toString());
        template.setDate(stringToLocalDate(binding.date.getText().toString()));
        template.setValue(stringToBigDecimal(binding.value.getText().toString()));
        template.setDescription(binding.description.getText().toString());
        template.setUrl(binding.url.getText().toString());
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        List<ValidatingTextInputLayout> layouts = new ArrayList<>();
        layouts.add(binding.nameLayout);
        if (StringUtils.isTextNotEmpty(binding.date.getText())) {
            layouts.add(binding.dateLayout);
        }
        if (StringUtils.isTextNotEmpty(binding.value.getText())) {
            layouts.add(binding.valueLayout);
        }
        return layouts;
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }
}
