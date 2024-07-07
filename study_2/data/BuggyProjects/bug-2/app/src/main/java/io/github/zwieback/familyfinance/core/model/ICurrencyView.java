package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import io.github.zwieback.familyfinance.core.model.restriction.CurrencyRestriction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_currency")
public interface ICurrencyView extends IBaseEntity {

    @Bindable
    @Column(nullable = false, length = CurrencyRestriction.NAME_MAX_LENGTH)
    String getName();

    @Bindable
    @Column(nullable = false, length = CurrencyRestriction.DESCRIPTION_MAX_LENGTH)
    String getDescription();
}
