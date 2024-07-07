package io.github.zwieback.familyfinance.business.account.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.mikepenz.iconics.view.IconicsImageView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.activity.helper.AccountTypeHelper;
import io.github.zwieback.familyfinance.business.account.adapter.AccountProvider;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.person.activity.PersonActivity;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.type.AccountType;
import io.github.zwieback.familyfinance.databinding.ActivityEditAccountBinding;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.CURRENCY_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.PERSON_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_CURRENCY_ID;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_PERSON_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.nonNullId;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToBigDecimal;
import static io.github.zwieback.familyfinance.util.NumberUtils.stringToInt;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;

public class AccountEditActivity extends
        EntityFolderEditActivity<Account, ActivityEditAccountBinding> {

    public static final String INPUT_ACCOUNT_ID = "accountId";
    public static final String OUTPUT_ACCOUNT_ID = "resultAccountId";

    @Override
    protected int getTitleStringId() {
        return R.string.account_activity_edit_title;
    }

    @Override
    protected int getBindingLayoutId() {
        return R.layout.activity_edit_account;
    }

    @Override
    protected String getExtraInputId() {
        return INPUT_ACCOUNT_ID;
    }

    @Override
    protected String getExtraOutputId() {
        return OUTPUT_ACCOUNT_ID;
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    protected EntityProvider<Account> createProvider() {
        return new AccountProvider(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACCOUNT_CODE:
                int parentId = extractOutputId(resultIntent, RESULT_ACCOUNT_ID);
                loadParent(parentId);
                break;
            case CURRENCY_CODE:
                int currencyId = extractOutputId(resultIntent, RESULT_CURRENCY_ID);
                loadCurrency(currencyId);
                break;
            case PERSON_CODE:
                int ownerId = extractOutputId(resultIntent, RESULT_PERSON_ID);
                loadOwner(ownerId);
                break;
        }
    }

    public void onParentClick(View view) {
        Intent intent = new Intent(this, AccountActivity.class);
        intent.putExtra(EntityActivity.INPUT_REGULAR_SELECTABLE, false);
        intent.putExtra(EntityFolderActivity.INPUT_PROHIBITED_FOLDER_ID, entity.getId());
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    private void onParentRemoved() {
        entity.setParent(null);
        binding.parentLayout.setError(null);
    }

    public void onCurrencyClick(View view) {
        Intent intent = new Intent(this, CurrencyActivity.class);
        startActivityForResult(intent, CURRENCY_CODE);
    }

    public void onOwnerClick(View view) {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivityForResult(intent, PERSON_CODE);
    }

    private void loadCurrency(int currencyId) {
        loadEntity(Currency.class, currencyId, foundCurrency -> entity.setCurrency(foundCurrency));
    }

    private void loadOwner(int ownerId) {
        loadEntity(Person.class, ownerId, foundOwner -> entity.setOwner(foundOwner));
    }

    private void loadParent(int parentId) {
        if (nonNullId(parentId)) {
            loadEntity(Account.class, parentId, foundAccount -> {
                entity.setParent(foundAccount);
                binding.parentLayout.setError(null);
            });
        }
    }

    @Override
    protected void createEntity() {
        int parentId = extractInputId(INPUT_PARENT_ID);
        boolean folder = extractInputBoolean(INPUT_IS_FOLDER);
        Account account = new Account();
        account.setActive(true);
        account.setFolder(folder);
        account.setType(AccountType.UNDEFINED_ACCOUNT);
        if (!folder) {
            account.setInitialBalance(BigDecimal.ZERO);
            loadOwner(databasePrefs.getPersonId());
            loadCurrency(databasePrefs.getCurrencyId());
        }
        bind(account);
        loadParent(parentId);
        disableLayout(binding.parentLayout, R.string.hint_parent_disabled);
    }

    @Override
    protected void bind(Account account) {
        entity = account;
        binding.setAccount(account);
        provider.setupIcon(binding.icon.getIcon(), account);
        super.bind(account);
    }

    @Override
    protected void setupBindings() {
        if (!entity.isFolder()) {
            binding.currency.setOnClearTextListener(() -> entity.setCurrency(null));
            binding.owner.setOnClearTextListener(() -> entity.setOwner(null));
        } else {
            disableLayout(binding.ownerLayout, R.string.hint_owner_disabled);
            disableLayout(binding.currencyLayout, R.string.hint_currency_disabled);
            disableLayout(binding.initialBalanceLayout, R.string.hint_initial_balance_disabled);
            disableLayout(binding.accountTypeLayout);
            disableLayout(binding.numberLayout, R.string.hint_account_number_disabled);
            disableLayout(binding.paymentSystemLayout, R.string.hint_payment_system_disabled);
            disableLayout(binding.cardNumberLayout, R.string.hint_card_number_disabled);
        }
        binding.icon.setOnClickListener(this::onSelectIconClick);
        binding.parent.setOnClickListener(this::onParentClick);
        binding.parent.setOnClearTextListener(this::onParentRemoved);
        binding.currency.setOnClickListener(this::onCurrencyClick);
        binding.owner.setOnClickListener(this::onOwnerClick);
        binding.parentLayout.setValidator(this::isParentValid);
        binding.accountType.setSelection(AccountTypeHelper.getAccountTypeIndex(entity));
    }

    @Override
    protected void updateEntityProperties(Account account) {
        account.setActive(binding.active.isChecked());
        account.setName(binding.name.getText().toString());
        account.setOrderCode(stringToInt(binding.orderCode.getText().toString()));
        if (!account.isFolder()) {
            account.setInitialBalance(
                    stringToBigDecimal(binding.initialBalance.getText().toString()));
            account.setType(AccountTypeHelper.getAccountType(binding.accountType));
            account.setNumber(binding.number.getText().toString());
            account.setPaymentSystem(binding.paymentSystem.getText().toString());
            account.setCardNumber(binding.cardNumber.getText().toString());
        }
    }

    @Override
    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        List<ValidatingTextInputLayout> layouts = new ArrayList<>(Arrays.asList(binding.parentLayout,
                binding.nameLayout, binding.orderCodeLayout));
        if (!entity.isFolder()) {
            layouts.addAll(Arrays.asList(binding.currencyLayout, binding.ownerLayout,
                    binding.initialBalanceLayout));
            if (isTextNotEmpty(binding.number.getText().toString())) {
                layouts.add(binding.numberLayout);
            }
            if (isTextNotEmpty(binding.cardNumber.getText().toString())) {
                layouts.add(binding.cardNumberLayout);
            }
        }
        return Collections.unmodifiableList(layouts);
    }

    @Override
    protected IconicsImageView getIconView() {
        return binding.icon;
    }

    private boolean isParentValid(String input) {
        return isParentValid(input, (Account) entity.getParent(), Account.$TYPE.getName());
    }

    @Override
    protected ValidatingTextInputLayout getParentLayout() {
        return binding.parentLayout;
    }

    @Override
    protected boolean isParentInsideItself(int parentId, int newParentId) {
        return isParentInsideItself(newParentId, Account.ID,
                Account.PARENT_ID.eq(parentId).and(Account.FOLDER.eq(true)),
                this::isParentInsideItself);
    }
}
