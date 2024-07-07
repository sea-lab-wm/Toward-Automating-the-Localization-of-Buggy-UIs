package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import io.github.zwieback.familyfinance.core.model.restriction.CurrencyRestriction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.PropertyNameStyle;
import io.requery.Table;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@Table(name = "currency")
public interface ICurrency extends IBaseEntity {

    @Bindable
    @Column(nullable = false, length = CurrencyRestriction.NAME_MAX_LENGTH)
    String getName();

    @Bindable
    @Column(nullable = false, length = CurrencyRestriction.DESCRIPTION_MAX_LENGTH)
    String getDescription();
}
