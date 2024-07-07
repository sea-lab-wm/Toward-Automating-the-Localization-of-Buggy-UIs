package io.github.zwieback.familyfinance.business.account.activity;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.filter.AccountFilter;
import io.github.zwieback.familyfinance.business.account.fragment.AccountFragment;
import io.github.zwieback.familyfinance.business.account.lifecycle.destroyer.AccountAsParentDestroyer;
import io.github.zwieback.familyfinance.business.account.lifecycle.destroyer.AccountFromExpenseOperationsDestroyer;
import io.github.zwieback.familyfinance.business.account.listener.OnAccountClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.AccountView;

import static io.github.zwieback.familyfinance.business.account.filter.AccountFilter.ACCOUNT_FILTER;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ACCOUNT_ID;
import static io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity.INPUT_IS_FOLDER;
import static io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity.INPUT_PARENT_ID;

public class AccountActivity
        extends EntityFolderActivity<AccountView, Account, AccountFilter, AccountFragment>
        implements OnAccountClickListener {

    public static final String INPUT_ONLY_ACTIVE = "inputOnlyActive";

    @Override
    protected int getTitleStringId() {
        return R.string.account_activity_title;
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return ACCOUNT_FILTER;
    }

    @NonNull
    @Override
    protected AccountFilter createDefaultFilter() {
        AccountFilter filter = new AccountFilter();
        filter.setOnlyActive(extractBoolean(getIntent().getExtras(), INPUT_ONLY_ACTIVE, false));
        return filter;
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_ACCOUNT_ID;
    }

    @Override
    protected String getFragmentTag() {
        return String.format("%s_%s", getLocalClassName(), filter.getParentId());
    }

    @Override
    protected AccountFragment createFragment() {
        return AccountFragment.newInstance(filter);
    }

    @Override
    protected void addEntity(int parentId, boolean isFolder) {
        super.addEntity(parentId, isFolder);
        Intent intent = new Intent(this, AccountEditActivity.class);
        intent.putExtra(INPUT_PARENT_ID, parentId);
        intent.putExtra(INPUT_IS_FOLDER, isFolder);
        startActivity(intent);
    }

    @Override
    protected void editEntity(AccountView account) {
        super.editEntity(account);
        Intent intent = new Intent(this, AccountEditActivity.class);
        intent.putExtra(AccountEditActivity.INPUT_ACCOUNT_ID, account.getId());
        startActivity(intent);
    }

    @Override
    protected Class<Account> getClassOfRegularEntity() {
        return Account.class;
    }

    @Override
    protected EntityDestroyer<Account> createDestroyer(AccountView account) {
        if (account.isFolder()) {
            return new AccountAsParentDestroyer(this, data);
        }
        return new AccountFromExpenseOperationsDestroyer(this, data);
    }
}
