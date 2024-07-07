package io.github.zwieback.familyfinance.business.operation.service.provider.exception;

import io.github.zwieback.familyfinance.core.model.type.OperationType;

public class UndefinedOperationProviderException extends IllegalArgumentException {

    private static final long serialVersionUID = -5379482490980416368L;

    public UndefinedOperationProviderException(OperationType type) {
        super("No provider is defined for an operation of type " + type);
    }
}
