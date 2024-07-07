package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToExchangeRateConverter;
import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToWorthConverter;
import io.github.zwieback.familyfinance.core.model.converter.LocalDateConverter;
import io.github.zwieback.familyfinance.core.model.restriction.AccountRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.ArticleRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.CurrencyRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.OperationRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.PersonRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.TemplateRestriction;
import io.github.zwieback.familyfinance.core.model.type.TemplateType;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Nullable;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_template")
public interface ITemplateView extends IBaseEntity {

    @Nullable
    @Column(name = "account_id")
    Integer getAccountId();

    @Bindable
    @Nullable
    @Column(name = "account_name", length = AccountRestriction.NAME_MAX_LENGTH)
    String getAccountName();

    @Nullable
    @Column(name = "transfer_account_id")
    Integer getTransferAccountId();

    @Bindable
    @Nullable
    @Column(name = "transfer_account_name", length = AccountRestriction.NAME_MAX_LENGTH)
    String getTransferAccountName();

    @Nullable
    @Column(name = "article_id")
    Integer getArticleId();

    @Bindable
    @Nullable
    @Column(name = "article_name", length = ArticleRestriction.NAME_MAX_LENGTH)
    String getArticleName();

    @Nullable
    @Column(name = "article_parent_id")
    Integer getArticleParentId();

    @Bindable
    @Nullable
    @Column(name = "article_parent_name", length = ArticleRestriction.NAME_MAX_LENGTH)
    String getArticleParentName();

    @Nullable
    @Column(name = "owner_id")
    Integer getOwnerId();

    @Bindable
    @Nullable
    @Column(name = "owner_name", length = PersonRestriction.NAME_MAX_LENGTH)
    String getOwnerName();

    @Nullable
    @Column(name = "exchange_rate_id")
    Integer getExchangeRateId();

    @Bindable
    @Nullable
    @Column(name = "exchange_rate_value")
    @Convert(BigDecimalToExchangeRateConverter.class)
    BigDecimal getExchangeRateValue();

    @Nullable
    @Column(name = "currency_id")
    Integer getCurrencyId();

    @Bindable
    @Nullable
    @Column(name = "currency_name", length = CurrencyRestriction.NAME_MAX_LENGTH)
    String getCurrencyName();

    @Bindable
    @Column(nullable = false, length = TemplateRestriction.NAME_MAX_LENGTH)
    String getName();

    @Column(name = "_type", nullable = false, length = TemplateRestriction.TYPE_MAX_LENGTH)
    TemplateType getType();

    @Bindable
    @Nullable
    @Column(name = "_date")
    @Convert(LocalDateConverter.class)
    LocalDate getDate();

    @Bindable
    @Nullable
    @Column(name = "_value")
    @Convert(BigDecimalToWorthConverter.class)
    BigDecimal getValue();

    @Bindable
    @Nullable
    @Column(length = OperationRestriction.DESCRIPTION_MAX_LENGTH)
    String getDescription();

    @Bindable
    @Nullable
    @Column(length = OperationRestriction.URL_MAX_LENGTH)
    String getUrl();
}
