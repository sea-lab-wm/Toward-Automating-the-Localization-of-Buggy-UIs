package io.github.zwieback.familyfinance.business.person.query;

import io.github.zwieback.familyfinance.core.model.PersonView;
import io.github.zwieback.familyfinance.core.query.EntityFolderQueryBuilder;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class PersonQueryBuilder extends EntityFolderQueryBuilder<PersonQueryBuilder, PersonView> {

    public static PersonQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new PersonQueryBuilder(data);
    }

    private PersonQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<PersonView> getEntityClass() {
        return PersonView.class;
    }

    @Override
    protected QueryExpression<Integer> getParentIdColumn() {
        return PersonView.PARENT_ID;
    }

    @Override
    protected Limit<ReactiveResult<PersonView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<PersonView>>> where) {
        return where.orderBy(PersonView.ORDER_CODE);
    }
}
