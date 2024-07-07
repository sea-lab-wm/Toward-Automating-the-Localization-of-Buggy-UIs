package io.github.zwieback.familyfinance.business.currency.query;

import io.github.zwieback.familyfinance.core.model.CurrencyView;
import io.github.zwieback.familyfinance.core.query.EntityQueryBuilder;
import io.requery.Persistable;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class CurrencyQueryBuilder extends EntityQueryBuilder<CurrencyView> {

    public static CurrencyQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new CurrencyQueryBuilder(data);
    }

    private CurrencyQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<CurrencyView> getEntityClass() {
        return CurrencyView.class;
    }

    @Override
    protected WhereAndOr<ReactiveResult<CurrencyView>>
    buildWhere(Where<ReactiveResult<CurrencyView>> select) {
        return select.where(CurrencyView.ID.gt(0));
    }

    @Override
    protected Limit<ReactiveResult<CurrencyView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<CurrencyView>>> where) {
        return where.orderBy(CurrencyView.ID);
    }
}
