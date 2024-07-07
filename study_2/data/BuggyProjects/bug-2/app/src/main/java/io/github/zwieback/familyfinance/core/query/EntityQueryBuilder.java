package io.github.zwieback.familyfinance.core.query;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.requery.Persistable;
import io.requery.query.JoinAndOr;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Result;
import io.requery.query.Selection;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public abstract class EntityQueryBuilder<E extends IBaseEntity> {

    protected final ReactiveEntityStore<Persistable> data;

    protected EntityQueryBuilder(ReactiveEntityStore<Persistable> data) {
        this.data = data;
    }

    protected abstract Class<E> getEntityClass();

    protected boolean isJoinRequired() {
        return false;
    }

    private Selection<ReactiveResult<E>> buildSelect() {
        return data.select(getEntityClass());
    }

    protected JoinAndOr<ReactiveResult<E>> buildJoin(Selection<ReactiveResult<E>> select) {
        return null;
    }

    protected abstract WhereAndOr<ReactiveResult<E>> buildWhere(Where<ReactiveResult<E>> select);

    protected abstract Limit<ReactiveResult<E>> buildOrderBy(OrderBy<Limit<ReactiveResult<E>>> where);

    public Result<E> build() {
        Selection<ReactiveResult<E>> select = buildSelect();
        WhereAndOr<ReactiveResult<E>> where;
        if (isJoinRequired()) {
            JoinAndOr<ReactiveResult<E>> joinAndOr = buildJoin(select);
            where = buildWhere(joinAndOr);
        } else {
            where = buildWhere(select);
        }
        Limit<ReactiveResult<E>> orderBy = buildOrderBy(where);
        return orderBy.get();
    }
}
