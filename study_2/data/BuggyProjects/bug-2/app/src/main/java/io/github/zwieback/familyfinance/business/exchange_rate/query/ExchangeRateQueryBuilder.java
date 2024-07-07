package io.github.zwieback.familyfinance.business.exchange_rate.query;

import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.ExchangeRateView;
import io.github.zwieback.familyfinance.core.query.EntityQueryBuilder;
import io.requery.Persistable;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class ExchangeRateQueryBuilder extends EntityQueryBuilder<ExchangeRateView> {

    public static ExchangeRateQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new ExchangeRateQueryBuilder(data);
    }

    @Nullable
    private Integer currencyId;
    @Nullable
    private LocalDate startDate;
    @Nullable
    private LocalDate endDate;
    @Nullable
    private BigDecimal startValue;
    @Nullable
    private BigDecimal endValue;

    private ExchangeRateQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<ExchangeRateView> getEntityClass() {
        return ExchangeRateView.class;
    }

    public final ExchangeRateQueryBuilder setCurrencyId(@Nullable Integer currencyId) {
        this.currencyId = currencyId;
        return this;
    }

    public final ExchangeRateQueryBuilder setStartDate(@Nullable LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public final ExchangeRateQueryBuilder setEndDate(@Nullable LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public final ExchangeRateQueryBuilder setStartValue(@Nullable BigDecimal startValue) {
        this.startValue = startValue;
        return this;
    }

    public final ExchangeRateQueryBuilder setEndValue(@Nullable BigDecimal endValue) {
        this.endValue = endValue;
        return this;
    }

    @Override
    protected WhereAndOr<ReactiveResult<ExchangeRateView>>
    buildWhere(Where<ReactiveResult<ExchangeRateView>> select) {
        WhereAndOr<ReactiveResult<ExchangeRateView>> result =
                select.where(ExchangeRateView.ID.gt(0));
        if (currencyId != null) {
            result = result.and(ExchangeRateView.CURRENCY_ID.eq(currencyId));
        }
        if (startDate != null) {
            result = result.and(ExchangeRateView.DATE.greaterThanOrEqual(startDate));
        }
        if (endDate != null) {
            result = result.and(ExchangeRateView.DATE.lessThanOrEqual(endDate));
        }
        if (startValue != null) {
            result = result.and(ExchangeRateView.VALUE.greaterThanOrEqual(startValue));
        }
        if (endValue != null) {
            result = result.and(ExchangeRateView.VALUE.lessThanOrEqual(endValue));
        }
        return result;
    }

    @Override
    protected Limit<ReactiveResult<ExchangeRateView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<ExchangeRateView>>> where) {
        return where.orderBy(ExchangeRateView.DATE.desc());
    }
}
