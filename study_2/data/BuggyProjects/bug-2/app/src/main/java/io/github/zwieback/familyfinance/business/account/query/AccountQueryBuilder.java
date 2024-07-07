package io.github.zwieback.familyfinance.business.account.query;

import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.AccountView;
import io.github.zwieback.familyfinance.core.query.EntityFolderQueryBuilder;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class AccountQueryBuilder
        extends EntityFolderQueryBuilder<AccountQueryBuilder, AccountView> {

    @Nullable
    private Integer ownerId;
    private boolean onlyActive;

    public static AccountQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new AccountQueryBuilder(data);
    }

    private AccountQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<AccountView> getEntityClass() {
        return AccountView.class;
    }

    public final AccountQueryBuilder setOwnerId(@Nullable Integer ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public final AccountQueryBuilder setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
        return this;
    }

    @Override
    protected QueryExpression<Integer> getParentIdColumn() {
        return Account.PARENT_ID;
    }

    @Override
    protected WhereAndOr<ReactiveResult<AccountView>>
    buildWhere(Where<ReactiveResult<AccountView>> select) {
        WhereAndOr<ReactiveResult<AccountView>> result = super.buildWhere(select);
        if (ownerId != null) {
            result = select
                    .where(AccountView.PARENT_ID.eq(parentId).and(AccountView.OWNER_ID.isNull()))
                    .or(AccountView.PARENT_ID.eq(parentId).and(AccountView.OWNER_ID.eq(ownerId)));
        }
        if (onlyActive) {
            result = result.and(AccountView.ACTIVE.eq(true));
        }
        return result;
    }

    @Override
    protected Limit<ReactiveResult<AccountView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<AccountView>>> where) {
        return where.orderBy(AccountView.ORDER_CODE);
    }
}
