package io.github.zwieback.familyfinance.business.account.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.account.adapter.AccountAdapter;
import io.github.zwieback.familyfinance.business.account.filter.AccountFilter;
import io.github.zwieback.familyfinance.business.account.listener.OnAccountClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFolderFragment;
import io.github.zwieback.familyfinance.core.model.AccountView;
import io.github.zwieback.familyfinance.databinding.ItemAccountBinding;

import static io.github.zwieback.familyfinance.business.account.filter.AccountFilter.ACCOUNT_FILTER;

public class AccountFragment extends EntityFolderFragment<AccountView, AccountFilter,
        ItemAccountBinding, OnAccountClickListener, AccountAdapter> {

    public static AccountFragment newInstance(AccountFilter filter) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = createArguments(ACCOUNT_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected AccountAdapter createEntityAdapter() {
        AccountFilter filter = extractFilter(ACCOUNT_FILTER);
        return new AccountAdapter(context, clickListener, data, filter);
    }
}
