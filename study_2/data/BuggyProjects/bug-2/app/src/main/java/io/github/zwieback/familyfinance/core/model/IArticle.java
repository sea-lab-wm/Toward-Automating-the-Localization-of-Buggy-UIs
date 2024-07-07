package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToWorthConverter;
import io.github.zwieback.familyfinance.core.model.restriction.ArticleRestriction;
import io.github.zwieback.familyfinance.core.model.type.ArticleType;
import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.OneToOne;
import io.requery.PropertyNameStyle;
import io.requery.Table;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@Table(name = "article")
public interface IArticle extends IBaseEntityFolder {

    @Bindable
    @ForeignKey
    @OneToOne(mappedBy = "id", cascade = CascadeAction.NONE)
    @Column(name = "parent_id")
    IArticle getParent();

    @Column(name = "_type", nullable = false, length = ArticleRestriction.TYPE_MAX_LENGTH)
    ArticleType getType();

    @Bindable
    @Column(nullable = false, length = ArticleRestriction.NAME_MAX_LENGTH)
    String getName();

    /**
     * Workaround for constraints of SQLite
     *
     * @see <a href="https://stackoverflow.com/a/16299031/8035065">
     * SQLite upper() alike function for international characters
     * </a>
     */
    @Column(name = "name_ascii", nullable = false,
            length = ArticleRestriction.NAME_ASCII_MAX_LENGTH)
    String getNameAscii();

    /**
     * {@code nullable} only if {@link #isFolder()}
     */
    @Bindable
    @Column(name = "default_value")
    @Convert(BigDecimalToWorthConverter.class)
    BigDecimal getDefaultValue();
}
