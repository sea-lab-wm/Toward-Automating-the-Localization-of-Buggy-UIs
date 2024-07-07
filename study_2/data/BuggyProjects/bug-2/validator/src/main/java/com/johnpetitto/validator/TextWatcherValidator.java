package com.johnpetitto.validator;

import android.text.Editable;
import android.text.TextWatcher;

public class TextWatcherValidator implements TextWatcher {

    private final ValidatingTextInputLayout layout;

    public TextWatcherValidator(ValidatingTextInputLayout layout) {
        this.layout = layout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // pass
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // pass
    }

    @Override
    public void afterTextChanged(Editable s) {
        layout.validate();
    }
}
