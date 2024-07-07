package io.github.zwieback.familyfinance.business.template.query;

import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.github.zwieback.familyfinance.core.query.EntityQueryBuilder;
import io.requery.Persistable;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class TemplateQueryBuilder extends EntityQueryBuilder<TemplateView> {

    public static TemplateQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new TemplateQueryBuilder(data);
    }

    private TemplateQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<TemplateView> getEntityClass() {
        return TemplateView.class;
    }

    @Override
    protected WhereAndOr<ReactiveResult<TemplateView>>
    buildWhere(Where<ReactiveResult<TemplateView>> select) {
        return select.where(TemplateView.ID.gt(0));
    }

    @Override
    protected Limit<ReactiveResult<TemplateView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<TemplateView>>> where) {
        return where.orderBy(TemplateView.TYPE, TemplateView.NAME);
    }
}
