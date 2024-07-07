package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToWorthConverter;
import io.github.zwieback.familyfinance.core.model.converter.LocalDateConverter;
import io.github.zwieback.familyfinance.core.model.restriction.OperationRestriction;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.ManyToOne;
import io.requery.Nullable;
import io.requery.OneToOne;
import io.requery.PropertyNameStyle;
import io.requery.Table;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@Table(name = "operation")
public interface IOperation extends IBaseEntity {

    /**
     * Not {@code null} only if {@link #getType()} in
     * ({@link OperationType#TRANSFER_EXPENSE_OPERATION},
     * {@link OperationType#TRANSFER_INCOME_OPERATION})
     */
    @ForeignKey
    @Nullable
    @OneToOne(mappedBy = "id", cascade = CascadeAction.NONE)
    @Column(name = "linked_transfer_operation_id")
    IOperation getLinkedTransferOperation();

    @Bindable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "account_id", nullable = false)
    IAccount getAccount();

    @Bindable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "article_id", nullable = false)
    IArticle getArticle();

    @Bindable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "owner_id", nullable = false)
    IPerson getOwner();

    @Bindable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "exchange_rate_id", nullable = false)
    IExchangeRate getExchangeRate();

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
