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
import io.github.zwieback.familyfinance.business.article.activity.ExpenseArticleActivity;
import io.github.zwieback.familyfinance.business.operation.service.provider.ExpenseOperationProvider;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.databinding.ActivityEditExpenseOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ARTICLE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.bigDecimalToString;
import static io.github.zwieback.familyfinance.util.NumberUtils.isNullId;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;

public class ExpenseOperationEditActivity
        extends OperationEditActivity<ActivityEditExpenseOperationBinding> {

    public static final String INPUT_EXPENSE_OPERATION_ID = "expenseOperationId";
    public static final String INPUT_EXPENSE_ACCOUNT_ID = "expenseAccountId";
    public static final String INPUT_EXPENSE_ARTICLE_ID = "expenseArticleId";
    public static final String INPUT_EXPENSE_OWNER_ID = "expenseOwnerId";
    public static final String INPUT_EXPENSE_CURRENCY_ID = "expenseCurrencyId";
    public static final String INPUT_EXPENSE_EXCHANGE_RATE_ID = "expenseExchangeRateId";
    public static final String INPUT_EXPENSE_VALUE = "expenseValue";
    public static final String INPUT_EXPENSE_DATE = "expenseDate";
    public static final String INPUT_EXPENSE_DESCRIPTION = "expenseDescription";
    public static final String INPUT_EXPENSE_URL = "expenseUrl";
    public static final String OUTPUT_EXPENSE_OPERATION_ID = "resultExpenseOperationId";

    @Override
    protected int getTitleStringId() {
        return R.string.expense_operation_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_expense_operation;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_EXPENSE_OPERATION_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_EXPENSE_OPERATION_ID;
    }

    @Override
    protected EntityProvider<Operation> createProvider() {
        return new ExpenseOperationProvider(this);
    }

    @Override
    OperationType getOperationType() {
        return OperationType.EXPENSE_OPERATION;
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
        Intent intent = new Intent(this, ExpenseArticleActivity.class);
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

    private int extractExpenseAccountId() {
        return extractInputId(INPUT_EXPENSE_ACCOUNT_ID, databasePrefs.getAccountId());
    }

    private int extractExpenseArticleId() {
        return extractInputId(INPUT_EXPENSE_ARTICLE_ID);
    }

    private int extractExpenseOwnerId() {
        return extractInputId(INPUT_EXPENSE_OWNER_ID, databasePrefs.getPersonId());
    }

    private int extractExpenseCurrencyId() {
        return extractInputId(INPUT_EXPENSE_CURRENCY_ID, databasePrefs.getCurrencyId());
    }

    private int extractExpenseExchangeRateId() {
        return extractInputId(INPUT_EXPENSE_EXCHANGE_RATE_ID);
    }

    @NonNull
    private LocalDate extractExpenseDate() {
        return extractInputDate(INPUT_EXPENSE_DATE);
    }

    @Nullable
    private BigDecimal extractExpenseValue() {
        return extractInputBigDecimal(INPUT_EXPENSE_VALUE);
    }

    @Nullable
    private String extractExpenseDescription() {
        return extractInputString(INPUT_EXPENSE_DESCRIPTION);
    }

    @Nullable
    private String extractExpenseUrl() {
        return extractInputString(INPUT_EXPENSE_URL);
    }

    @Override
    protected void createEntity() {
        super.createEntity();
        loadAccount(extractExpenseAccountId());
        loadArticle(extractExpenseArticleId());
        loadOwner(extractExpenseOwnerId());
        int exchangeRateId = extractExpenseExchangeRateId();
        if (isNullId(exchangeRateId)) {
            loadCurrency(extractExpenseCurrencyId());
        } else {
            loadExchangeRate(exchangeRateId);
        }
    }

    @Override
    Operation createOperation() {
        Operation operation = super.createOperation();
        operation.setDate(extractExpenseDate());
        operation.setValue(extractExpenseValue());
        operation.setDescription(extractExpenseDescription());
        operation.setUrl(extractExpenseUrl());
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
                binding.expenseAccountLayout, binding.ownerLayout,
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
