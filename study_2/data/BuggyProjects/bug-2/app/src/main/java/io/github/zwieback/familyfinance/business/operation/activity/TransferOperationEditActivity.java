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
import io.github.zwieback.familyfinance.business.operation.service.provider.TransferOperationProvider;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.databinding.ActivityEditTransferOperationBinding;
import io.github.zwieback.familyfinance.widget.ClearableEditText;
import io.reactivex.functions.Consumer;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.EXPENSE_ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.INCOME_ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.isNullId;

public class TransferOperationEditActivity
        extends OperationEditActivity<ActivityEditTransferOperationBinding> {

    public static final String INPUT_TRANSFER_OPERATION_ID = "transferExpenseOperationId";
    public static final String INPUT_EXPENSE_ACCOUNT_ID = "expenseAccountId";
    public static final String INPUT_INCOME_ACCOUNT_ID = "incomeAccountId";
    public static final String INPUT_EXPENSE_OWNER_ID = "expenseOwnerId";
    public static final String INPUT_EXPENSE_CURRENCY_ID = "expenseCurrencyId";
    public static final String INPUT_EXPENSE_EXCHANGE_RATE_ID = "expenseExchangeRateId";
    public static final String INPUT_EXPENSE_VALUE = "expenseValue";
    public static final String INPUT_EXPENSE_DATE = "expenseDate";
    public static final String INPUT_EXPENSE_DESCRIPTION = "expenseDescription";
    public static final String INPUT_EXPENSE_URL = "expenseUrl";
    public static final String OUTPUT_TRANSFER_OPERATION_ID = "resultTransferExpenseOperationId";

    /**
     * internal {@link #entity} is alias for expense operation
     */
    private Operation incomeOperation;

    @Override
    protected int getTitleStringId() {
        return R.string.transfer_operation_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_transfer_operation;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_TRANSFER_OPERATION_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_TRANSFER_OPERATION_ID;
    }

    @Override
    protected EntityProvider<Operation> createProvider() {
        return new TransferOperationProvider(this);
    }

    @Override
    OperationType getOperationType() {
        return OperationType.TRANSFER_EXPENSE_OPERATION;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case EXPENSE_ACCOUNT_CODE:
                int expenseAccountId = extractOutputId(resultIntent, RESULT_ACCOUNT_ID);
                loadExpenseAccount(expenseAccountId);
                break;
            case INCOME_ACCOUNT_CODE:
                int incomeAccountId = extractOutputId(resultIntent, RESULT_ACCOUNT_ID);
                loadIncomeAccount(incomeAccountId);
                break;
        }
    }

    public void onExpenseAccountClick(View view) {
        startAccountActivity(EXPENSE_ACCOUNT_CODE);
    }

    public void onIncomeAccountClick(View view) {
        startAccountActivity(INCOME_ACCOUNT_CODE);
    }

    private Consumer<Account> onSuccessfulExpenseAccountFound() {
        return foundAccount -> {
            entity.setAccount(foundAccount);
            if (foundAccount.getOwner() != null) {
                loadOwner(foundAccount.getOwner().getId());
            }
        };
    }

    private Consumer<Account> onSuccessfulIncomeAccountFound() {
        return foundAccount -> incomeOperation.setAccount(foundAccount);
    }

    private Consumer<Article> onSuccessfulArticleFound() {
        return foundArticle -> entity.setArticle(foundArticle);
    }

    private void loadExpenseAccount(int accountId) {
        loadEntity(Account.class, accountId, onSuccessfulExpenseAccountFound());
    }

    private void loadIncomeAccount(int accountId) {
        loadEntity(Account.class, accountId, onSuccessfulIncomeAccountFound());
    }

    private void loadDefaultArticle() {
        int transferArticleId = databasePrefs.getTransferArticleId();
        loadEntity(Article.class, transferArticleId, onSuccessfulArticleFound());
    }

    private int extractExpenseAccountId() {
        return extractInputId(INPUT_EXPENSE_ACCOUNT_ID, databasePrefs.getAccountId());
    }

    private int extractIncomeAccountId() {
        return extractInputId(INPUT_INCOME_ACCOUNT_ID);
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
        createIncomeOperation();
        loadDefaultArticle();
        loadExpenseAccount(extractExpenseAccountId());
        loadIncomeAccount(extractIncomeAccountId());
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

    private void createIncomeOperation() {
        incomeOperation = new Operation();
        incomeOperation.setType(OperationType.TRANSFER_INCOME_OPERATION);
        bindIncomeOperation(incomeOperation);
    }

    @Override
    protected void loadEntity(int expenseOperationId) {
        loadEntity(Operation.class, expenseOperationId, this::onSuccessfulExpenseOperationFound);
    }

    private void onSuccessfulExpenseOperationFound(Operation expenseOperation) {
        bind(expenseOperation);
        loadEntity(Operation.class, expenseOperation.getLinkedTransferOperation().getId(),
                this::bindIncomeOperation);
    }

    @Override
    protected void bind(Operation operation) {
        entity = operation;
        binding.setExpenseOperation(operation);
        provider.setupIcon(binding.icon.getIcon(), operation);
        super.bind(operation);
    }

    private void bindIncomeOperation(Operation operation) {
        incomeOperation = operation;
        binding.setIncomeOperation(operation);
        setupIncomeOperationBindings();
    }

    @Override
    protected void setupBindings() {
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.owner.setOnClickListener(this::onOwnerClick);
        binding.expenseAccount.setOnClickListener(this::onExpenseAccountClick);
        binding.expenseAccount.setOnClearTextListener(() -> entity.setAccount(null));
        binding.date.setOnClickListener(this::onDateClick);
        binding.currency.setOnClickListener(this::onCurrencyClick);
        binding.exchangeRate.setOnClickListener(this::onExchangeRateClick);
        super.setupBindings();
    }

    private void setupIncomeOperationBindings() {
        binding.incomeAccount.setOnClickListener(this::onIncomeAccountClick);
        binding.incomeAccount.setOnClearTextListener(() -> incomeOperation.setAccount(null));
    }

    @Override
    protected void updateEntityProperties(Operation operation) {
        super.updateEntityProperties(operation);
        incomeOperation.setArticle(operation.getArticle());
        incomeOperation.setOwner(operation.getOwner());
        incomeOperation.setExchangeRate(operation.getExchangeRate());
        incomeOperation.setDate(operation.getDate());
        incomeOperation.setValue(operation.getValue());
        incomeOperation.setDescription(operation.getDescription());
        incomeOperation.setUrl(operation.getUrl());
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        return Arrays.asList(
                binding.ownerLayout,
                binding.expenseAccountLayout, binding.incomeAccountLayout,
                binding.valueLayout, binding.dateLayout,
                binding.currencyLayout, binding.exchangeRateLayout);
    }

    @Override
    protected Consumer<Operation> onSuccessfulSaving() {
        return expenseOperation -> {
            incomeOperation.setLinkedTransferOperation(expenseOperation);
            saveEntity(incomeOperation, onSuccessfulIncomeOperationSaving(expenseOperation));
        };
    }

    private Consumer<Operation> onSuccessfulIncomeOperationSaving(Operation expenseOperation) {
        return incomeOperation -> {
            expenseOperation.setLinkedTransferOperation(incomeOperation);
            saveEntity(expenseOperation, onSuccessfulExpenseOperationSaving());
        };
    }

    private Consumer<Operation> onSuccessfulExpenseOperationSaving() {
        return this::closeActivity;
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
