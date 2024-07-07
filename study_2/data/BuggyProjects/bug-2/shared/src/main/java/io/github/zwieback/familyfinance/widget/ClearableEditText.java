package io.github.zwieback.familyfinance.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import io.github.zwieback.familyfinance.core.R;
import io.github.zwieback.familyfinance.widget.adapter.TextWatcherAdapter;
import io.github.zwieback.familyfinance.widget.listener.OnClearTextListener;
import io.github.zwieback.familyfinance.widget.listener.TextWatcherListener;

import static io.github.zwieback.familyfinance.util.StringUtils.EMPTY;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;

public class ClearableEditText extends IconicsEditText implements TextWatcherListener,
        View.OnFocusChangeListener {

    @Nullable
    private OnClearTextListener onClearTextListener;

    public ClearableEditText(Context context) {
        super(context);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    void initialize(Context context) {
        super.initialize(context);
        addTextChangedListener(new TextWatcherAdapter(this));
    }

    @NonNull
    @Override
    protected String getIconName() {
        return "faw_times_circle";
    }

    @Override
    protected int getIconSize() {
        return R.dimen.clear_icon_size;
    }

    @Override
    protected int getIconColor() {
        return R.color.icon_inside_edit_text;
    }

    @Override
    public void onIconClick() {
        setText(EMPTY);
    }

    @Override
    public void onTextChanged(String text) {
        if (isFocusable()) {
            if (isFocused()) {
                setIconVisible(isTextNotEmpty(text));
            }
        } else {
            setIconVisible(isTextNotEmpty(text));
        }
        if (isTextEmpty(text) && onClearTextListener != null) {
            onClearTextListener.onTextCleared();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        setIconVisible(hasFocus && isTextNotEmpty(getText()));
    }

    public void setOnClearTextListener(@Nullable OnClearTextListener onClearTextListener) {
        this.onClearTextListener = onClearTextListener;
    }
}
