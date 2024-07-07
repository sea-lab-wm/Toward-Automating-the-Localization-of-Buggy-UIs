package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import io.github.zwieback.familyfinance.core.model.restriction.PersonRestriction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.Nullable;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_person")
public interface IPersonView extends IBaseEntityFolder {

    @Nullable
    @Column(name = "parent_id")
    Integer getParentId();

    @Bindable
    @Nullable
    @Column(name = "parent_name", length = PersonRestriction.NAME_MAX_LENGTH)
    String getParentName();

    @Bindable
    @Column(nullable = false, length = PersonRestriction.NAME_MAX_LENGTH)
    String getName();

    @Bindable
    @Column(name = "order_code", nullable = false)
    int getOrderCode();
}
