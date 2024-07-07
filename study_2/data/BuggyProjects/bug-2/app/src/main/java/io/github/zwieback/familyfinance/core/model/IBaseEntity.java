package io.github.zwieback.familyfinance.core.model;

import android.databinding.Observable;
import android.os.Parcelable;

import java.io.Serializable;

import io.github.zwieback.familyfinance.core.model.restriction.BaseRestriction;
import io.github.zwieback.familyfinance.core.model.setter.IconNameSetter;
import io.requery.Column;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;
import io.requery.Superclass;

@Superclass
public interface IBaseEntity<T extends IBaseEntity> extends Observable, Persistable, Parcelable,
        Serializable, IconNameSetter<T> {

    @Key
    @Generated
    int getId();

    @Column(name = "icon_name", length = BaseRestriction.ICON_NAME_MAX_LENGTH)
    String getIconName();
}
