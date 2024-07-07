package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToExchangeRateConverter;
import io.github.zwieback.familyfinance.core.model.converter.LocalDateConverter;
import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.ManyToOne;
import io.requery.PropertyNameStyle;
import io.requery.Table;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@Table(name = "exchange_rate")
public interface IExchangeRate extends IBaseEntity {

    @Bindable
    @ManyToOne(cascade = CascadeAction.NONE)
    @Column(name = "currency_id", nullable = false)
    ICurrency getCurrency();

    @Bindable
    @Column(name = "_value", nullable = false)
    @Convert(BigDecimalToExchangeRateConverter.class)
    BigDecimal getValue();

    @Bindable
    @Column(name = "_date", nullable = false)
    @Convert(LocalDateConverter.class)
    LocalDate getDate();
}
