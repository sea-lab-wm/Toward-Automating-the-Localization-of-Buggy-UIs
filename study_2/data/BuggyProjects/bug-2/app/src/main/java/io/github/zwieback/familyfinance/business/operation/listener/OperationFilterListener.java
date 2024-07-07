package io.github.zwieback.familyfinance.business.operation.listener;

import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.core.listener.EntityFilterListener;

public interface OperationFilterListener<F extends OperationFilter>
        extends EntityFilterListener<F> {
}
