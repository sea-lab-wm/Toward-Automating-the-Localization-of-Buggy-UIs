package io.github.zwieback.familyfinance.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.iconics.IconicsDrawable;

import io.github.zwieback.familyfinance.core.R;
import io.github.zwieback.familyfinance.util.ViewUtils;
import io.github.zwieback.familyfinance.widget.listener.OnIconClickListener;

public abstract class IconicsEditText extends TextInputEditText implements View.OnTouchListener,
        OnIconClickListener {

    /**
     * Do not remove {@link SuppressWarnings} because
     * it initialized in {@link #initialize} method.
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    private IconicsDrawable icon;
    private boolean iconVisible;

    public IconicsEditText(Context context) {
        this(context, null);
    }

    public IconicsEditText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public IconicsEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            initialize(context);
        }
    }

    @CallSuper
    void initialize(Context context) {
        icon = createIcon(context);
        setIconVisible(false);
        setOnTouchListener(this);
    }

    @NonNull
    private IconicsDrawable createIcon(Context context) {
        return new IconicsDrawable(context, getIconName())
                .sizeRes(getIconSize())
                .colorRes(getIconColor());
    }

    @NonNull
    protected abstract String getIconName();

    @DimenRes
    protected abstract int getIconSize();

    @ColorRes
    protected abstract int getIconColor();

    private void repaintIcon(@Nullable Drawable icon) {
        iconVisible = icon != null;
        Drawable[] drawables = TextViewCompat.getCompoundDrawablesRelative(this);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(this,
                drawables[0], drawables[1], icon, drawables[3]
        );
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isClickedOnIcon(event)) {
            onIconClick();
            return true;
        }
        return false;
    }

    private boolean isClickedOnIcon(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return false;
        }
        boolean ltr = ViewUtils.isLeftToRightLayoutDirection(this);
        int x = (int) event.getX();
        int y = (int) event.getY();
        int left = ltr ? getWidth() - getPaddingRight() - icon.getIntrinsicWidth() : 0;
        int right = ltr ? getWidth() : getPaddingLeft() + icon.getIntrinsicWidth();
        return x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
    }

    void setIconVisible(boolean visible) {
        if (!isEnabled()) {
            return;
        }
        boolean wasVisible = iconVisible;
        if (visible != wasVisible) {
            repaintIcon(visible ? icon : null);
        }
    }
}
