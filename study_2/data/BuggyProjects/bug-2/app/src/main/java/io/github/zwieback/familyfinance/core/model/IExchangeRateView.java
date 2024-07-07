package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToExchangeRateConverter;
import io.github.zwieback.familyfinance.core.model.converter.LocalDateConverter;
import io.github.zwieback.familyfinance.core.model.restriction.CurrencyRestriction;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_exchange_rate")
public interface IExchangeRateView extends IBaseEntity {

    @Bindable
    @Column(name = "_value", nullable = false)
    @Convert(BigDecimalToExchangeRateConverter.class)
    BigDecimal getValue();

    @Bindable
    @Column(name = "_date", nullable = false)
    @Convert(LocalDateConverter.class)
    LocalDate getDate();

    @Column(name = "currency_id", nullable = false)
    int getCurrencyId();

    @Bindable
    @Column(name = "currency_name", nullable = false, length = CurrencyRestriction.NAME_MAX_LENGTH)
    String getCurrencyName();
}
