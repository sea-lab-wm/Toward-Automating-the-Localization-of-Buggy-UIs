package io.github.zwieback.familyfinance.business.dashboard.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.activity.AccountActivity;
import io.github.zwieback.familyfinance.business.chart.activity.ChartActivity;
import io.github.zwieback.familyfinance.business.dashboard.activity.drawer.DrawerCreator;
import io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter;
import io.github.zwieback.familyfinance.business.operation.activity.ExpenseOperationActivity;
import io.github.zwieback.familyfinance.business.operation.activity.FlowOfFundsOperationActivity;
import io.github.zwieback.familyfinance.business.operation.activity.IncomeOperationActivity;
import io.github.zwieback.familyfinance.business.operation.activity.TransferOperationActivity;
import io.github.zwieback.familyfinance.business.operation.activity.helper.ExpenseOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.IncomeOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.TransferOperationHelper;
import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;
import io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;
import io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter;
import io.github.zwieback.familyfinance.business.sms_pattern.activity.SmsPatternActivity;
import io.github.zwieback.familyfinance.business.template.activity.TemplateActivity;
import io.github.zwieback.familyfinance.core.activity.DataActivityWrapper;
import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter.EXCHANGE_RATE_FILTER;
import static io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter.EXPENSE_OPERATION_FILTER;
import static io.github.zwieback.familyfinance.business.operation.filter.FlowOfFundsOperationFilter.FLOW_OF_FUNDS_OPERATION_FILTER;
import static io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter.INCOME_OPERATION_FILTER;
import static io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter.TRANSFER_OPERATION_FILTER;
import static io.github.zwieback.familyfinance.core.activity.EntityActivity.INPUT_READ_ONLY;
import static io.github.zwieback.familyfinance.core.activity.EntityActivity.INPUT_REGULAR_SELECTABLE;

@RuntimePermissions
public class DashboardActivity extends DataActivityWrapper {

    public static final String RESULT_CURRENCY_ID = "resultCurrencyId";
    public static final String RESULT_EXCHANGE_RATE_ID = "resultExchangeRateId";
    public static final String RESULT_PERSON_ID = "resultPersonId";
    public static final String RESULT_ACCOUNT_ID = "resultAccountId";
    public static final String RESULT_ARTICLE_ID = "resultArticleId";
    public static final String RESULT_OPERATION_ID = "resultOperationId";
    public static final String RESULT_TEMPLATE_ID = "resultTemplateId";
    public static final String RESULT_SMS_PATTERN_ID = "resultSmsPatternId";

    public static final int CURRENCY_CODE = 101;
    public static final int CURRENCY_EDIT_CODE = 102;
    public static final int EXCHANGE_RATE_CODE = 201;
    public static final int EXCHANGE_RATE_EDIT_CODE = 202;
    public static final int PERSON_CODE = 301;
    public static final int PERSON_EDIT_CODE = 302;
    public static final int ACCOUNT_CODE = 401;
    public static final int INCOME_ACCOUNT_CODE = 402;
    public static final int EXPENSE_ACCOUNT_CODE = 403;
    public static final int ACCOUNT_EDIT_CODE = 404;
    public static final int ARTICLE_CODE = 501;
    public static final int INCOME_ARTICLE_CODE = 502;
    public static final int EXPENSE_ARTICLE_CODE = 503;
    public static final int ARTICLE_EDIT_CODE = 505;
    public static final int INCOME_OPERATION_CODE = 601;
    public static final int EXPENSE_OPERATION_CODE = 602;
    public static final int TRANSFER_OPERATION_CODE = 603;
    public static final int FLOW_OF_FUNDS_OPERATION_CODE = 604;
    public static final int INCOME_OPERATION_EDIT_CODE = 605;
    public static final int EXPENSE_OPERATION_EDIT_CODE = 606;
    public static final int TRANSFER_OPERATION_EDIT_CODE = 607;
    public static final int ICONICS_CODE = 701;
    public static final int BACKUP_PATH_CODE = 801;
    public static final int TEMPLATE_CODE = 901;
    public static final int TEMPLATE_EDIT_CODE = 902;
    public static final int SMS_CODE = 1001;
    public static final int SMS_EDIT_CODE = 1002;

    @Nullable
    private ExchangeRateFilter exchangeRateFilter;
    @Nullable
    private ExpenseOperationFilter expenseOperationFilter;
    @Nullable
    private IncomeOperationFilter incomeOperationFilter;
    @Nullable
    private TransferOperationFilter transferOperationFilter;
    @Nullable
    private FlowOfFundsOperationFilter flowOfFundsOperationFilter;

    private IncomeOperationHelper incomeOperationHelper;
    private ExpenseOperationHelper expenseOperationHelper;
    private TransferOperationHelper transferOperationHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DrawerCreator(this).createDrawer(findToolbar());
        init(savedInstanceState);
        bindOnClickListeners();
        DashboardActivityPermissionsDispatcher.registerSmsReceiverWithPermissionCheck(this);
    }

    @Override
    protected void setupContentView() {
        setContentView(R.layout.activity_dashboard);
    }

    @Override
    protected int getTitleStringId() {
        return R.string.app_name;
    }

    @Override
    protected boolean isDisplayHomeAsUpEnabled() {
        return false;
    }

    private void init(@Nullable Bundle savedInstanceState) {
        exchangeRateFilter = loadFilter(savedInstanceState, EXCHANGE_RATE_FILTER);
        expenseOperationFilter = loadFilter(savedInstanceState, EXPENSE_OPERATION_FILTER);
        incomeOperationFilter = loadFilter(savedInstanceState, INCOME_OPERATION_FILTER);
        transferOperationFilter = loadFilter(savedInstanceState, TRANSFER_OPERATION_FILTER);
        flowOfFundsOperationFilter = loadFilter(savedInstanceState, FLOW_OF_FUNDS_OPERATION_FILTER);
        incomeOperationHelper = new IncomeOperationHelper(this, data);
        expenseOperationHelper = new ExpenseOperationHelper(this, data);
        transferOperationHelper = new TransferOperationHelper(this, data);
    }

    private <F extends EntityFilter> F loadFilter(@Nullable Bundle savedInstanceState,
                                                  String filterName) {
        if (savedInstanceState == null) {
            return null;
        }
        return savedInstanceState.getParcelable(filterName);
    }

    private void bindOnClickListeners() {
        findViewById(R.id.select_account).setOnClickListener(this::onSelectAccountClick);
        findViewById(R.id.select_expenses).setOnClickListener(this::onSelectExpensesClick);
        findViewById(R.id.add_expense).setOnClickListener(this::onAddExpenseClick);
        findViewById(R.id.select_income).setOnClickListener(this::onSelectIncomesClick);
        findViewById(R.id.add_income).setOnClickListener(this::onAddIncomeClick);
        findViewById(R.id.select_transfers).setOnClickListener(this::onSelectTransfersClick);
        findViewById(R.id.add_transfer).setOnClickListener(this::onAddTransferClick);
        findViewById(R.id.select_flow_of_funds).setOnClickListener(this::onSelectFlowOfFundsClick);
        findViewById(R.id.select_templates).setOnClickListener(this::onSelectTemplatesClick);
        findViewById(R.id.select_sms_patterns).setOnClickListener(this::onSelectSmsPatternsClick);
        findViewById(R.id.select_charts).setOnClickListener(this::onSelectChartsClick);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXCHANGE_RATE_FILTER, exchangeRateFilter);
        outState.putParcelable(EXPENSE_OPERATION_FILTER, expenseOperationFilter);
        outState.putParcelable(INCOME_OPERATION_FILTER, incomeOperationFilter);
        outState.putParcelable(TRANSFER_OPERATION_FILTER, transferOperationFilter);
        outState.putParcelable(FLOW_OF_FUNDS_OPERATION_FILTER, flowOfFundsOperationFilter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case EXCHANGE_RATE_CODE:
                if (resultIntent.hasExtra(EXCHANGE_RATE_FILTER)) {
                    exchangeRateFilter = resultIntent.getParcelableExtra(EXCHANGE_RATE_FILTER);
                }
                break;
            case PERSON_CODE:
                break;
            case ACCOUNT_CODE:
                break;
            case INCOME_ARTICLE_CODE:
                break;
            case EXPENSE_ARTICLE_CODE:
                break;
            case INCOME_OPERATION_CODE:
                if (resultIntent.hasExtra(INCOME_OPERATION_FILTER)) {
                    incomeOperationFilter = resultIntent.getParcelableExtra(INCOME_OPERATION_FILTER);
                }
                break;
            case EXPENSE_OPERATION_CODE:
                if (resultIntent.hasExtra(EXPENSE_OPERATION_FILTER)) {
                    expenseOperationFilter = resultIntent.getParcelableExtra(EXPENSE_OPERATION_FILTER);
                }
                break;
            case TRANSFER_OPERATION_CODE:
                if (resultIntent.hasExtra(TRANSFER_OPERATION_FILTER)) {
                    transferOperationFilter = resultIntent.getParcelableExtra(TRANSFER_OPERATION_FILTER);
                }
                break;
            case FLOW_OF_FUNDS_OPERATION_CODE:
                if (resultIntent.hasExtra(FLOW_OF_FUNDS_OPERATION_FILTER)) {
                    flowOfFundsOperationFilter = resultIntent.getParcelableExtra(FLOW_OF_FUNDS_OPERATION_FILTER);
                }
                break;
            case TEMPLATE_CODE:
                break;
            case SMS_CODE:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DashboardActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.RECEIVE_SMS)
    public void registerSmsReceiver() {
        // empty method because SmsReceiver already registered in the manifest
        // this method is required only for the PermissionsDispatcher
    }

    @OnShowRationale(Manifest.permission.RECEIVE_SMS)
    void showRationaleForReceiveSms(PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_receive_sms_rationale)
                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
                .show();
    }

    public void onSelectAccountClick(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    public void onSelectExpensesClick(View view) {
        Intent intent = new Intent(this, ExpenseOperationActivity.class);
        intent.putExtra(EXPENSE_OPERATION_FILTER, expenseOperationFilter);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, EXPENSE_OPERATION_CODE);
    }

    public void onSelectIncomesClick(View view) {
        Intent intent = new Intent(this, IncomeOperationActivity.class);
        intent.putExtra(INCOME_OPERATION_FILTER, incomeOperationFilter);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, INCOME_OPERATION_CODE);
    }

    public void onSelectTransfersClick(View view) {
        Intent intent = new Intent(this, TransferOperationActivity.class);
        intent.putExtra(TRANSFER_OPERATION_FILTER, transferOperationFilter);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, TRANSFER_OPERATION_CODE);
    }

    public void onSelectFlowOfFundsClick(View view) {
        Intent intent = new Intent(this, FlowOfFundsOperationActivity.class);
        intent.putExtra(FLOW_OF_FUNDS_OPERATION_FILTER, flowOfFundsOperationFilter);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, FLOW_OF_FUNDS_OPERATION_CODE);
    }

    public void onSelectTemplatesClick(View view) {
        Intent intent = new Intent(this, TemplateActivity.class);
        intent.putExtra(INPUT_READ_ONLY, false);
        intent.putExtra(INPUT_REGULAR_SELECTABLE, false);
        startActivityForResult(intent, TEMPLATE_CODE);
    }

    public void onSelectSmsPatternsClick(View view) {
        Intent intent = new Intent(this, SmsPatternActivity.class);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, SMS_CODE);
    }

    public void onSelectChartsClick(View view) {
        Intent intent = new Intent(this, ChartActivity.class);
        startActivity(intent);
    }

    public void onAddExpenseClick(View view) {
        Intent intent = expenseOperationHelper.getIntentToAdd(expenseOperationFilter);
        startActivityForResult(intent, EXPENSE_OPERATION_EDIT_CODE);
    }

    public void onAddIncomeClick(View view) {
        Intent intent = incomeOperationHelper.getIntentToAdd(incomeOperationFilter);
        startActivityForResult(intent, INCOME_OPERATION_EDIT_CODE);
    }

    public void onAddTransferClick(View view) {
        Intent intent = transferOperationHelper.getIntentToAdd(transferOperationFilter);
        startActivityForResult(intent, TRANSFER_OPERATION_EDIT_CODE);
    }

    @Nullable
    public ExchangeRateFilter getExchangeRateFilter() {
        return exchangeRateFilter;
    }
}
