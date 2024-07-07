package io.github.zwieback.familyfinance.core.activity.exception;

public class ReadOnlyException extends IllegalStateException {

    private static final long serialVersionUID = -8708939504644359255L;

    public ReadOnlyException() {
        super("The method can't be executed because the readOnly flag is set");
    }
}
