package io.github.zwieback.familyfinance.core.fragment;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.core.adapter.EntityFolderAdapter;
import io.github.zwieback.familyfinance.core.filter.EntityFolderFilter;
import io.github.zwieback.familyfinance.core.listener.EntityFolderClickListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntityFolder;

import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.intToIntegerId;
import static io.github.zwieback.familyfinance.util.NumberUtils.integerToIntId;

public abstract class EntityFolderFragment<
        ENTITY extends IBaseEntityFolder,
        FILTER extends EntityFolderFilter,
        BINDING extends ViewDataBinding,
        LISTENER extends EntityFolderClickListener<ENTITY>,
        ADAPTER extends EntityFolderAdapter<ENTITY, FILTER, BINDING, LISTENER>>
        extends EntityFragment<ENTITY, FILTER, BINDING, LISTENER, ADAPTER> {

    private static final String PARENT_ID_ARG = "parentId";

    /**
     * Workaround to get parentId when activity recreated.
     */
    @Nullable
    public Integer getParentId() {
        int id = getArguments() == null
                ? ID_AS_NULL
                : getArguments().getInt(PARENT_ID_ARG, ID_AS_NULL);
        return intToIntegerId(id);
    }

    /**
     * Workaround to get parentId when activity recreated.
     */
    protected static <FILTER extends EntityFolderFilter> Bundle createArguments(String filterName,
                                                                                FILTER filter) {
        Bundle args = new Bundle();
        args.putParcelable(filterName, filter);
        args.putInt(PARENT_ID_ARG, integerToIntId(filter.getParentId()));
        return args;
    }
}
