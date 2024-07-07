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
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Nullable;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_operation")
public interface IOperationView extends IBaseEntity {

    /**
     * Not {@code null} only if {@link #getType()} in
     * ({@link OperationType#TRANSFER_EXPENSE_OPERATION},
     * {@link OperationType#TRANSFER_INCOME_OPERATION})
     */
    @Nullable
    @Column(name = "linked_transfer_operation_id")
    Integer getLinkedTransferOperationId();

    @Column(name = "account_id", nullable = false)
    int getAccountId();

    @Bindable
    @Column(name = "account_name", nullable = false, length = AccountRestriction.NAME_MAX_LENGTH)
    String getAccountName();

    @Column(name = "article_id", nullable = false)
    int getArticleId();

    @Bindable
    @Column(name = "article_name", nullable = false, length = ArticleRestriction.NAME_MAX_LENGTH)
    String getArticleName();

    @Nullable
    @Column(name = "article_parent_id")
    Integer getArticleParentId();

    @Bindable
    @Nullable
    @Column(name = "article_parent_name", length = ArticleRestriction.NAME_MAX_LENGTH)
    String getArticleParentName();

    @Column(name = "owner_id", nullable = false)
    int getOwnerId();

    @Bindable
    @Column(name = "owner_name", nullable = false, length = PersonRestriction.NAME_MAX_LENGTH)
    String getOwnerName();

    @Column(name = "exchange_rate_id", nullable = false)
    int getExchangeRateId();

    @Bindable
    @Column(name = "exchange_rate_value", nullable = false)
    @Convert(BigDecimalToExchangeRateConverter.class)
    BigDecimal getExchangeRateValue();

    @Column(name = "currency_id", nullable = false)
    int getCurrencyId();

    @Bindable
    @Column(name = "currency_name", nullable = false, length = CurrencyRestriction.NAME_MAX_LENGTH)
    String getCurrencyName();

    @Column(name = "_type", nullable = false, length = OperationRestriction.TYPE_MAX_LENGTH)
    OperationType getType();

    @Bindable
    @Column(name = "_date", nullable = false)
    @Convert(LocalDateConverter.class)
    LocalDate getDate();

    @Bindable
    @Column(name = "_value", nullable = false)
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
