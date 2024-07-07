package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import io.github.zwieback.familyfinance.core.model.restriction.PersonRestriction;
import io.requery.CascadeAction;
import io.requery.Column;
import io.requery.Entity;
import io.requery.ForeignKey;
import io.requery.OneToOne;
import io.requery.PropertyNameStyle;
import io.requery.Table;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@Table(name = "person")
public interface IPerson extends IBaseEntityFolder {

    @Bindable
    @ForeignKey
    @OneToOne(mappedBy = "id", cascade = CascadeAction.NONE)
    @Column(name = "parent_id")
    IPerson getParent();

    @Bindable
    @Column(nullable = false, length = PersonRestriction.NAME_MAX_LENGTH)
    String getName();

    @Bindable
    @Column(name = "order_code", nullable = false)
    int getOrderCode();
}
