package io.github.zwieback.familyfinance.business.operation.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.activity.IncomeArticleActivity;
import io.github.zwieback.familyfinance.business.operation.service.provider.IncomeOperationProvider;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.databinding.ActivityEditIncomeOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ARTICLE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.bigDecimalToString;
import static io.github.zwieback.familyfinance.util.NumberUtils.isNullId;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;

public class IncomeOperationEditActivity
        extends OperationEditActivity<ActivityEditIncomeOperationBinding> {

    public static final String INPUT_INCOME_OPERATION_ID = "incomeOperationId";
    public static final String INPUT_INCOME_ACCOUNT_ID = "incomeAccountId";
    public static final String INPUT_INCOME_ARTICLE_ID = "incomeArticleId";
    public static final String INPUT_INCOME_OWNER_ID = "incomeOwnerId";
    public static final String INPUT_INCOME_CURRENCY_ID = "incomeCurrencyId";
    public static final String INPUT_INCOME_EXCHANGE_RATE_ID = "incomeExchangeRateId";
    public static final String INPUT_INCOME_VALUE = "incomeValue";
    public static final String INPUT_INCOME_DATE = "incomeDate";
    public static final String INPUT_INCOME_DESCRIPTION = "incomeDescription";
    public static final String INPUT_INCOME_URL = "incomeUrl";
    public static final String OUTPUT_INCOME_OPERATION_ID = "resultIncomeOperationId";

    @Override
    protected int getTitleStringId() {
        return R.string.income_operation_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_income_operation;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_INCOME_OPERATION_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_INCOME_OPERATION_ID;
    }

    @Override
    protected EntityProvider<Operation> createProvider() {
        return new IncomeOperationProvider(this);
    }

    @Override
    OperationType getOperationType() {
        return OperationType.INCOME_OPERATION;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACCOUNT_CODE:
                int accountId = extractOutputId(resultIntent, RESULT_ACCOUNT_ID);
                loadAccount(accountId);
                break;
            case ARTICLE_CODE:
                int articleId = extractOutputId(resultIntent, RESULT_ARTICLE_ID);
                loadArticle(articleId);
                break;
        }
    }

    public void onAccountClick(View view) {
        startAccountActivity(ACCOUNT_CODE);
    }

    public void onArticleClick(View view) {
        Intent intent = new Intent(this, IncomeArticleActivity.class);
        intent.putExtra(EntityActivity.INPUT_READ_ONLY, false);
        startActivityForResult(intent, ARTICLE_CODE);
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
            if (foundAccount.getOwner() != null) {
                loadOwner(foundAccount.getOwner().getId());
            }
        };
    }

    private void loadArticle(int articleId) {
        loadEntity(Article.class, articleId, onSuccessfulArticleFound());
    }

    private void loadAccount(int accountId) {
        loadEntity(Account.class, accountId, onSuccessfulAccountFound());
    }

    private int extractIncomeAccountId() {
        return extractInputId(INPUT_INCOME_ACCOUNT_ID, databasePrefs.getAccountId());
    }

    private int extractIncomeArticleId() {
        return extractInputId(INPUT_INCOME_ARTICLE_ID);
    }

    private int extractIncomeOwnerId() {
        return extractInputId(INPUT_INCOME_OWNER_ID, databasePrefs.getPersonId());
    }

    private int extractIncomeCurrencyId() {
        return extractInputId(INPUT_INCOME_CURRENCY_ID, databasePrefs.getCurrencyId());
    }

    private int extractIncomeExchangeRateId() {
        return extractInputId(INPUT_INCOME_EXCHANGE_RATE_ID);
    }

    @NonNull
    private LocalDate extractIncomeDate() {
        return extractInputDate(INPUT_INCOME_DATE);
    }

    @Nullable
    private BigDecimal extractIncomeValue() {
        return extractInputBigDecimal(INPUT_INCOME_VALUE);
    }

    @Nullable
    private String extractIncomeDescription() {
        return extractInputString(INPUT_INCOME_DESCRIPTION);
    }

    @Nullable
    private String extractIncomeUrl() {
        return extractInputString(INPUT_INCOME_URL);
    }

    @Override
    protected void createEntity() {
        super.createEntity();
        loadAccount(extractIncomeAccountId());
        loadAccount(extractIncomeAccountId());
        loadArticle(extractIncomeArticleId());
        loadOwner(extractIncomeOwnerId());
        int exchangeRateId = extractIncomeExchangeRateId();
        if (isNullId(exchangeRateId)) {
            loadCurrency(extractIncomeCurrencyId());
        } else {
            loadExchangeRate(exchangeRateId);
        }
    }

    @Override
    Operation createOperation() {
        Operation operation = super.createOperation();
        operation.setDate(extractIncomeDate());
        operation.setValue(extractIncomeValue());
        operation.setDescription(extractIncomeDescription());
        operation.setUrl(extractIncomeUrl());
        return operation;
    }

    @Override
    protected void bind(Operation operation) {
        entity = operation;
        binding.setOperation(operation);
        binding.value.setTextColor(provider.provideTextColor(operation));
        provider.setupIcon(binding.icon.getIcon(), operation);
        super.bind(operation);
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
        binding.owner.setOnClickListener(this::onOwnerClick);
        binding.date.setOnClickListener(this::onDateClick);
        binding.currency.setOnClickListener(this::onCurrencyClick);
        binding.exchangeRate.setOnClickListener(this::onExchangeRateClick);
        super.setupBindings();
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        return Arrays.asList(
                binding.articleNameLayout, binding.articleCategoryLayout,
                binding.incomeAccountLayout, binding.ownerLayout,
                binding.valueLayout, binding.dateLayout,
                binding.currencyLayout, binding.exchangeRateLayout);
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }

    @Override
    ClearableEditText getOwnerEdit() {
        return binding.owner;
    }

    @Override
    ClearableEditText getCurrencyEdit() {
        return binding.currency;
    }

    @Override
    ClearableEditText getExchangeRateEdit() {
        return binding.exchangeRate;
    }

    @Override
    EditText getDateEdit() {
        return binding.date;
    }

    @Override
    EditText getValueEdit() {
        return binding.value;
    }

    @Override
    EditText getDescriptionEdit() {
        return binding.description;
    }

    @Override
    EditText getUrlEdit() {
        return binding.url;
    }
}
