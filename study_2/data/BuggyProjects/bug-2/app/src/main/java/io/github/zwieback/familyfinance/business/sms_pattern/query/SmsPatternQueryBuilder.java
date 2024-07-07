package io.github.zwieback.familyfinance.business.sms_pattern.query;

import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.github.zwieback.familyfinance.core.model.SmsPatternView;
import io.github.zwieback.familyfinance.core.query.EntityQueryBuilder;
import io.requery.Persistable;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

public class SmsPatternQueryBuilder extends EntityQueryBuilder<SmsPatternView> {

    public static SmsPatternQueryBuilder create(ReactiveEntityStore<Persistable> data) {
        return new SmsPatternQueryBuilder(data);
    }

    @Nullable
    private String sender;
    private boolean useOrderByCommon;

    private SmsPatternQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<SmsPatternView> getEntityClass() {
        return SmsPatternView.class;
    }

    public SmsPatternQueryBuilder setSender(@Nullable String sender) {
        this.sender = sender;
        return this;
    }

    public SmsPatternQueryBuilder orderByCommon() {
        this.useOrderByCommon = true;
        return this;
    }

    @Override
    protected WhereAndOr<ReactiveResult<SmsPatternView>>
    buildWhere(Where<ReactiveResult<SmsPatternView>> select) {
        WhereAndOr<ReactiveResult<SmsPatternView>> result = select.where(SmsPatternView.ID.gt(0));
        if (sender != null) {
            result = result.and(SmsPattern.SENDER.equal(sender));
        }
        return result;
    }

    @Override
    protected Limit<ReactiveResult<SmsPatternView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<SmsPatternView>>> where) {
        if (useOrderByCommon) {
            return where.orderBy(SmsPatternView.SENDER, SmsPatternView.NAME, SmsPatternView.COMMON);
        }
        return where.orderBy(SmsPatternView.SENDER, SmsPatternView.NAME);
    }
}
