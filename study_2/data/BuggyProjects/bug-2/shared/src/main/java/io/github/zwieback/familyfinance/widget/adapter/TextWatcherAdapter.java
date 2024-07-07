package io.github.zwieback.familyfinance.widget.adapter;

import android.text.Editable;
import android.text.TextWatcher;

import io.github.zwieback.familyfinance.widget.listener.TextWatcherListener;

public class TextWatcherAdapter implements TextWatcher {

    private final TextWatcherListener listener;

    public TextWatcherAdapter(TextWatcherListener listener) {
        this.listener = listener;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        listener.onTextChanged(s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // pass
    }

    @Override
    public void afterTextChanged(Editable s) {
        // pass
    }
}
