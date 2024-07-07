package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.activity.AccountActivity;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.preference.custom.EntityActivityResultPreference;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ACCOUNT_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;

public class AccountPreference extends EntityActivityResultPreference<Account> {

    @SuppressWarnings("unused")
    public AccountPreference(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public AccountPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public AccountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public AccountPreference(Context context) {
        super(context);
    }

    @Override
    protected int getRequestCode() {
        return ACCOUNT_CODE;
    }

    @Override
    protected Intent getRequestIntent() {
        return new Intent(getContext(), AccountActivity.class)
                .putExtra(AccountActivity.INPUT_ONLY_ACTIVE, true)
                .putExtra(EntityFolderActivity.INPUT_FOLDER_SELECTABLE, false);
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_ACCOUNT_ID;
    }

    @Override
    protected int getSavedEntityId() {
        return databasePrefs.getAccountId();
    }

    @Override
    protected void saveEntityId(int accountId) {
        databasePrefs.setAccountId(accountId);
    }

    @Override
    protected Class<Account> getEntityClass() {
        return Account.class;
    }

    @Override
    protected String getEntityName(@NonNull Account account) {
        return account.getName();
    }

    @Override
    protected int getPreferenceTitleRes() {
        return R.string.account_id_preference_title;
    }
}
