package io.github.zwieback.familyfinance.core.model.setter;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;

public interface IconNameSetter<T extends IBaseEntity> {

    T setIconName(String iconName);
}
