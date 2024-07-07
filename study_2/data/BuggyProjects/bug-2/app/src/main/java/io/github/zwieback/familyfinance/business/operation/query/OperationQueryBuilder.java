package io.github.zwieback.familyfinance.business.operation.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.zwieback.familyfinance.core.model.AccountView;
import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.core.query.EntityQueryBuilder;
import io.github.zwieback.familyfinance.util.CollectionUtils;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.SqliteUtils;
import io.requery.Persistable;
import io.requery.meta.QueryAttribute;
import io.requery.meta.Type;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Tuple;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

abstract class OperationQueryBuilder<T extends OperationQueryBuilder>
        extends EntityQueryBuilder<OperationView> {

    @NonNull
    private LocalDate startDate;
    @NonNull
    private LocalDate endDate;
    @Nullable
    private BigDecimal startValue;
    @Nullable
    private BigDecimal endValue;
    @Nullable
    private List<OperationType> types;
    @Nullable
    private Integer articleId;
    @Nullable
    private Integer accountId;
    @Nullable
    private Integer ownerId;
    @Nullable
    private Integer currencyId;

    OperationQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
        startDate = DateUtils.startOfMonth();
        endDate = DateUtils.endOfMonth();
    }

    @Override
    protected final Class<OperationView> getEntityClass() {
        return OperationView.class;
    }

    @SuppressWarnings("unchecked")
    public final T setStartDate(@NonNull LocalDate startDate) {
        this.startDate = startDate;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setEndDate(@NonNull LocalDate endDate) {
        this.endDate = endDate;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setStartValue(@Nullable BigDecimal startValue) {
        this.startValue = startValue;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setEndValue(@Nullable BigDecimal endValue) {
        this.endValue = endValue;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setTypes(@Nullable List<OperationType> types) {
        this.types = types;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setArticleId(@Nullable Integer expenseArticleId) {
        this.articleId = expenseArticleId;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setAccountId(@Nullable Integer accountId) {
        this.accountId = accountId;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setOwnerId(@Nullable Integer ownerId) {
        this.ownerId = ownerId;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setCurrencyId(@Nullable Integer currencyId) {
        this.currencyId = currencyId;
        return (T) this;
    }

    @Override
    protected WhereAndOr<ReactiveResult<OperationView>>
    buildWhere(Where<ReactiveResult<OperationView>> select) {
        WhereAndOr<ReactiveResult<OperationView>> result =
                select.where(OperationView.DATE.greaterThanOrEqual(startDate)
                        .and(OperationView.DATE.lessThanOrEqual(endDate)));
        if (startValue != null) {
            result = result.and(OperationView.VALUE.greaterThanOrEqual(startValue));
        }
        if (endValue != null) {
            result = result.and(OperationView.VALUE.lessThanOrEqual(endValue));
        }
        if (types != null) {
            result = result.and(OperationView.TYPE.in(types));
        }
        if (articleId != null) {
            Set<Integer> articleIds = collectArticleIds(articleId);
            result = result.and(OperationView.ARTICLE_ID.in(articleIds));
        }
        if (accountId != null) {
            Set<Integer> accountIds = collectAccountIds(accountId);
            result = result.and(OperationView.ACCOUNT_ID.in(accountIds));
        }
        if (ownerId != null) {
            result = result.and(OperationView.OWNER_ID.eq(ownerId));
        }
        if (currencyId != null) {
            result = result.and(OperationView.CURRENCY_ID.eq(currencyId));
        }
        return result;
    }

    @Override
    protected Limit<ReactiveResult<OperationView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<OperationView>>> where) {
        return where.orderBy(OperationView.DATE.desc(), OperationView.ID.desc());
    }

    /**
     * Collect all the accounts (this and its children) recursively.
     *
     * @param parentId an id of parent account
     * @return all accounts
     */
    private Set<Integer> collectAccountIds(@NonNull Integer parentId) {
        return collectEntityIds(parentId, AccountView.$TYPE, AccountView.ID, AccountView.PARENT_ID,
                AccountView::getId, AccountView::getParentId);
    }

    /**
     * Collect all the articles (this and its children) recursively.
     *
     * @param parentId an id of parent article
     * @return all articles
     */
    private Set<Integer> collectArticleIds(@NonNull Integer parentId) {
        return collectEntityIds(parentId, ArticleView.$TYPE, ArticleView.ID, ArticleView.PARENT_ID,
                ArticleView::getId, ArticleView::getParentId);
    }

    /**
     * Workaround to get all the entities (this and its children) recursively without using CTE.
     * <p>
     * NOTE: Do not use the stream lib version 1.2.0 or above here, because
     * {@link Collectors#toMap(Function, Function)} will throw a NPE.
     * The NPE will be thrown because the {@literal parentId} can be {@code null},
     * but the result {@link Map} of {@link Collectors#toMap(Function, Function)}
     * doesn't allow {@code null} values.
     * Or you can rewrite the code without using the
     * {@link Collectors#toMap(Function, Function)} method.
     */
    private <E extends IBaseEntity> Set<Integer> collectEntityIds(
            @NonNull Integer parentId,
            Type<E> entityType,
            QueryAttribute<E, Integer> entityIdAttribute,
            QueryAttribute<E, Integer> parentIdAttribute,
            Function<E, Integer> getIdFunction,
            Function<E, Integer> getParentIdFunction) {

        if (SqliteUtils.cteSupported()) {
            return collectEntityIdsThroughCte(parentId, entityType.getName());
        }

        Iterator<E> iterator =
                data.select(entityType.getClassType(), entityIdAttribute, parentIdAttribute)
                        .get().iterator();

        Map<Integer, Integer> idWithParentIdMap = Stream.of(iterator)
                .collect(CollectionUtils.toMap(getIdFunction, getParentIdFunction));

        Set<Integer> result = new HashSet<>(collectChildIds(idWithParentIdMap, parentId));
        result.add(parentId);
        return result;
    }

    private List<Integer> collectChildIds(@NonNull Map<Integer, Integer> idWithParentIdMap,
                                          @NonNull Integer parentId) {
        List<Integer> childIds = Stream.of(idWithParentIdMap)
                .filter(entry -> parentId.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (childIds.isEmpty()) {
            return childIds;
        }

        List<Integer> result = new ArrayList<>(childIds);
        Stream.of(childIds)
                .forEach(childId -> result.addAll(collectChildIds(idWithParentIdMap, childId)));
        return result;
    }

    private Set<Integer> collectEntityIdsThroughCte(@NonNull Integer parentId,
                                                    @NonNull String tableName) {
        String query = "with recursive subtree" +
                " as (select id" +
                "       from :tableName" +
                "      where id = :parentId" +
                "      union all" +
                "     select child.id" +
                "       from :tableName as child" +
                "       join subtree on child.parent_id = subtree.id)" +
                " select id" +
                " from subtree";
        query = query.replaceAll(":parentId", String.valueOf(parentId));
        query = query.replaceAll(":tableName", tableName);

        ReactiveResult<Tuple> result = data.raw(query);
        return Stream.of(result.iterator())
                .map(tuple -> tuple.get("id").toString())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }
}
