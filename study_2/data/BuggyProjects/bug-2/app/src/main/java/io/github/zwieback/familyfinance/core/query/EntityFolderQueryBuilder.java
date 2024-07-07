package io.github.zwieback.familyfinance.core.query;

import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.core.model.IBaseEntityFolder;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public abstract class EntityFolderQueryBuilder<
        T extends EntityFolderQueryBuilder,
        E extends IBaseEntityFolder>
        extends EntityQueryBuilder<E> {

    @Nullable
    protected Integer parentId;

    protected EntityFolderQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    protected abstract QueryExpression<Integer> getParentIdColumn();

    protected WhereAndOr<ReactiveResult<E>> buildWhere(Where<ReactiveResult<E>> select) {
        return select.where(getParentIdColumn().eq(parentId));
    }

    @SuppressWarnings("unchecked")
    public final T setParentId(@Nullable Integer parentId) {
        this.parentId = parentId;
        return (T) this;
    }
}
