package io.github.zwieback.familyfinance.core.adapter;

import android.support.v7.widget.RecyclerView;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.requery.android.QueryRecyclerAdapter;
import io.requery.meta.Type;
import io.requery.query.Result;
import io.requery.sql.ResultSetIterator;

/**
 * Workaround to remember scroll position of RecyclerView.
 */
public abstract class QuerySyncRecyclerAdapter<
        ENTITY extends IBaseEntity,
        HOLDER extends RecyclerView.ViewHolder>
        extends QueryRecyclerAdapter<ENTITY, HOLDER> {

    protected QuerySyncRecyclerAdapter(Type<ENTITY> type) {
        super(type);
    }

    /**
     * Warning: this method is executed on the main thread.
     */
    public void querySync() {
        Result<ENTITY> result = performQuery();
        ResultSetIterator<ENTITY> iterator = (ResultSetIterator<ENTITY>) result.iterator();
        setResult(iterator);
    }
}
