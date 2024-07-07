package io.github.zwieback.familyfinance.core.model;

import io.requery.Column;
import io.requery.Superclass;

@Superclass
public interface IBaseEntityFolder extends IBaseEntity {

    @Column(name = "is_folder", nullable = false)
    boolean isFolder();
}
