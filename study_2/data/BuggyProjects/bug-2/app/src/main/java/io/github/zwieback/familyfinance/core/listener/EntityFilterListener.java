package io.github.zwieback.familyfinance.core.listener;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;

public interface EntityFilterListener<F extends EntityFilter> {

    void onApplyFilter(F filter);
}
