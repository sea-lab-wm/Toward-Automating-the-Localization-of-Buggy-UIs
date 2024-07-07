package io.github.zwieback.familyfinance.business.template.exception;

import io.github.zwieback.familyfinance.core.model.type.TemplateType;

public class UnsupportedTemplateTypeException extends RuntimeException {

    private static final long serialVersionUID = 3058114760405260932L;

    public UnsupportedTemplateTypeException(int id, TemplateType type) {
        super(String.format("Type '%s' of template with id '%s' not supported", type, id));
    }
}
