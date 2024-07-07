package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToWorthConverter;
import io.github.zwieback.familyfinance.core.model.converter.LocalDateConverter;
import io.github.zwieback.familyfinance.core.model.restriction.OperationRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.TemplateRestriction;
import io.github.zwieback.familyfinance.core.model.type.TemplateType;
import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.ManyToOne;
import io.requery.Nullable;
import io.requery.PropertyNameStyle;
import io.requery.Table;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@Table(name = "t_template")
public interface ITemplate extends IBaseEntity {

    @Bindable
    @Nullable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "account_id")
    IAccount getAccount();

    @Bindable
    @Nullable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "transfer_account_id")
    IAccount getTransferAccount();

    @Bindable
    @Nullable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "article_id")
    IArticle getArticle();

    @Bindable
    @Nullable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "owner_id")
    IPerson getOwner();

    @Bindable
    @Nullable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "exchange_rate_id")
    IExchangeRate getExchangeRate();

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
