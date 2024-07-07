package io.github.zwieback.familyfinance.core.fragment.exception;

public class FragmentWithoutArgumentsException extends IllegalArgumentException {

    private static final long serialVersionUID = 2352581929250982086L;

    public FragmentWithoutArgumentsException(Class fragmentClass) {
        super(fragmentClass.getSimpleName() + " has no arguments");
    }
}
