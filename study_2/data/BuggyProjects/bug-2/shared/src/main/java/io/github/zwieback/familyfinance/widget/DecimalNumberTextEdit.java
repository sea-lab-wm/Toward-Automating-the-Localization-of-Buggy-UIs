package io.github.zwieback.familyfinance.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.calculator.dialog.OnCalculationResultListener;
import io.github.zwieback.familyfinance.core.R;
import io.github.zwieback.familyfinance.util.DialogUtils;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.github.zwieback.familyfinance.widget.filter.DecimalNumberInputFilter;

public class DecimalNumberTextEdit extends IconicsEditText implements OnCalculationResultListener {

    public DecimalNumberTextEdit(Context context) {
        super(context);
    }

    public DecimalNumberTextEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DecimalNumberTextEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void initialize(Context context) {
        super.initialize(context);
        setInputType(EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
        setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
        setFilters(new InputFilter[]{new DecimalNumberInputFilter()});
        setIconVisible(true);
    }

    @NonNull
    @Override
    protected String getIconName() {
        return "cmd_calculator";
    }

    @Override
    protected int getIconSize() {
        return R.dimen.calculator_icon_size;
    }

    @Override
    protected int getIconColor() {
        return R.color.icon_inside_edit_text;
    }

    @Override
    public void onIconClick() {
        DialogUtils.showCalculatorDialog(getContext(), this,
                NumberUtils.stringToBigDecimal(this.getText().toString()));
    }

    @Override
    public void onCalculationResult(@Nullable BigDecimal result) {
        setText(NumberUtils.bigDecimalToString(result));
    }
}
