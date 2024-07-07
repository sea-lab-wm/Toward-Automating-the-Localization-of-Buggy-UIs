package io.github.zwieback.familyfinance.business.article.query;

import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.core.model.ArticleView;
import io.github.zwieback.familyfinance.core.model.type.ArticleType;
import io.github.zwieback.familyfinance.core.query.EntityFolderQueryBuilder;
import io.requery.Persistable;
import io.requery.meta.QueryExpression;
import io.requery.query.Limit;
import io.requery.query.OrderBy;
import io.requery.query.Where;
import io.requery.query.WhereAndOr;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;
import static io.github.zwieback.familyfinance.util.TransliterationUtils.transliterate;

public abstract class ArticleQueryBuilder<T extends ArticleQueryBuilder>
        extends EntityFolderQueryBuilder<T, ArticleView> {

    @Nullable
    private ArticleType type;
    @Nullable
    private String searchName;

    ArticleQueryBuilder(ReactiveEntityStore<Persistable> data) {
        super(data);
    }

    @Override
    protected final Class<ArticleView> getEntityClass() {
        return ArticleView.class;
    }

    @SuppressWarnings("unchecked")
    public final T setType(@Nullable ArticleType type) {
        this.type = type;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final T setSearchName(@Nullable String searchName) {
        this.searchName = transliterate(searchName);
        return (T) this;
    }

    @Override
    protected QueryExpression<Integer> getParentIdColumn() {
        return ArticleView.PARENT_ID;
    }

    @Override
    protected WhereAndOr<ReactiveResult<ArticleView>>
    buildWhere(Where<ReactiveResult<ArticleView>> select) {
        WhereAndOr<ReactiveResult<ArticleView>> where;
        if (isTextNotEmpty(searchName)) {
            where = select.where(ArticleView.NAME_ASCII.lower()
                    .like("%" + searchName.toLowerCase() + "%"));
        } else {
            where = super.buildWhere(select);
        }
        if (type != null) {
            return where.and(ArticleView.TYPE.eq(type));
        }
        return where;
    }

    @Override
    protected Limit<ReactiveResult<ArticleView>>
    buildOrderBy(OrderBy<Limit<ReactiveResult<ArticleView>>> where) {
        return where.orderBy(ArticleView.FOLDER.desc(), ArticleView.NAME);
    }
}
