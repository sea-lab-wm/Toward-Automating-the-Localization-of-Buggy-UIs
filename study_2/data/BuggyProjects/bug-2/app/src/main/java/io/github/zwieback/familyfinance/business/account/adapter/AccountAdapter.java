package io.github.zwieback.familyfinance.business.account.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.adapter.calculator.AccountBalanceCalculator;
import io.github.zwieback.familyfinance.business.account.adapter.calculator.NonOptimizedAccountBalanceCalculator;
import io.github.zwieback.familyfinance.business.account.filter.AccountFilter;
import io.github.zwieback.familyfinance.business.account.listener.OnAccountClickListener;
import io.github.zwieback.familyfinance.business.account.query.AccountQueryBuilder;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityFolderAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.AccountView;
import io.github.zwieback.familyfinance.databinding.ItemAccountBinding;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class AccountAdapter extends EntityFolderAdapter<AccountView, AccountFilter,
        ItemAccountBinding, OnAccountClickListener> {

    public AccountAdapter(Context context,
                          OnAccountClickListener clickListener,
                          ReactiveEntityStore<Persistable> data,
                          AccountFilter filter) {
        super(AccountView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<AccountView> createProvider(Context context) {
        return new AccountViewProvider(context);
    }

    @Override
    protected ItemAccountBinding inflate(LayoutInflater inflater) {
        return ItemAccountBinding.inflate(inflater);
    }

    @Override
    protected AccountView extractEntity(ItemAccountBinding binding) {
        return (AccountView) binding.getAccount();
    }

    @Override
    public Result<AccountView> performQuery() {
        return AccountQueryBuilder.create(data)
                .setParentId(parentId)
                .setOwnerId(filter.getOwnerId())
                .setOnlyActive(filter.isOnlyActive())
                .build();
    }

    @Override
    public void onBindViewHolder(AccountView account, BindingHolder<ItemAccountBinding> holder,
                                 int position) {
        holder.binding.setAccount(account);
        provider.setupIcon(holder.binding.icon.getIcon(), account);
        calculateAndShowBalance(account, holder);
    }

    private void calculateAndShowBalance(AccountView account,
                                         BindingHolder<ItemAccountBinding> holder) {
        if (account.isFolder()) {
            return;
        }
        AccountBalanceCalculator calculator =
                new NonOptimizedAccountBalanceCalculator(data, account);
        calculator.calculateBalance(showBalance(holder));
    }

    private Consumer<BigDecimal> showBalance(BindingHolder<ItemAccountBinding> holder) {
        return balance -> {
            holder.binding.setBalanceValue(balance);
            if (balance.signum() < 0) {
                @ColorInt int negativeBalanceColor =
                        ContextCompat.getColor(context, R.color.colorNegativeBalance);
                holder.binding.balance.setTextColor(negativeBalanceColor);
            }
        };
    }
}
