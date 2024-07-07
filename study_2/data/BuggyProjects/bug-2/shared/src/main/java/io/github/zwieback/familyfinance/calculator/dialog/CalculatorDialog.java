package io.github.zwieback.familyfinance.calculator.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.View;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.calculator.Operator;
import io.github.zwieback.familyfinance.calculator.stateful.OnInvalidateStateListener;
import io.github.zwieback.familyfinance.calculator.stateful.StatefulCalculator;
import io.github.zwieback.familyfinance.core.R;

public class CalculatorDialog extends AlertDialog implements DialogInterface.OnClickListener,
        OnInvalidateStateListener {

    private OnCalculationResultListener calculationResultListener;
    private StatefulCalculator calculator;
    private TextInputEditText numberEditText;

    public CalculatorDialog(@NonNull Context context,
                            @Nullable OnCalculationResultListener calculationResultListener,
                            @Nullable BigDecimal defaultValue) {
        this(context, 0, calculationResultListener, defaultValue);
    }

    private CalculatorDialog(@NonNull Context context,
                             @StyleRes int themeResId,
                             @Nullable OnCalculationResultListener listener,
                             @Nullable BigDecimal defaultValue) {
        super(context, resolveDialogTheme(context, themeResId));

        Context themeContext = getContext();
        View view = View.inflate(themeContext, R.layout.dialog_calculator, null);
        setView(view);

        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        calculationResultListener = listener;
        calculator = new StatefulCalculator(this, defaultValue);
        setupViews(view);
        onInvalidateState();
    }

    @Override
    public void onClick(@NonNull DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (calculationResultListener != null) {
                    calculator.updateCalculator();
                    calculationResultListener.onCalculationResult(calculator.calc());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    private void setupViews(View view) {
        numberEditText = view.findViewById(R.id.number);
        view.findViewById(R.id.clr).setOnClickListener(v -> calculator.clear());
        view.findViewById(R.id.del).setOnClickListener(v -> calculator.delete());
        view.findViewById(R.id.eq).setOnClickListener(v -> calculator.eq());
        view.findViewById(R.id.op_add).setOnClickListener(v -> calculator.changeOperator(Operator.ADD));
        view.findViewById(R.id.op_sub).setOnClickListener(v -> calculator.changeOperator(Operator.SUB));
        view.findViewById(R.id.op_mul).setOnClickListener(v -> calculator.changeOperator(Operator.MUL));
        view.findViewById(R.id.op_div).setOnClickListener(v -> calculator.changeOperator(Operator.DIV));
        view.findViewById(R.id.dec_point).setOnClickListener(v -> calculator.addDecimalSeparator());
        view.findViewById(R.id.digit_0).setOnClickListener(v -> calculator.addDigit("0"));
        view.findViewById(R.id.digit_1).setOnClickListener(v -> calculator.addDigit("1"));
        view.findViewById(R.id.digit_2).setOnClickListener(v -> calculator.addDigit("2"));
        view.findViewById(R.id.digit_3).setOnClickListener(v -> calculator.addDigit("3"));
        view.findViewById(R.id.digit_4).setOnClickListener(v -> calculator.addDigit("4"));
        view.findViewById(R.id.digit_5).setOnClickListener(v -> calculator.addDigit("5"));
        view.findViewById(R.id.digit_6).setOnClickListener(v -> calculator.addDigit("6"));
        view.findViewById(R.id.digit_7).setOnClickListener(v -> calculator.addDigit("7"));
        view.findViewById(R.id.digit_8).setOnClickListener(v -> calculator.addDigit("8"));
        view.findViewById(R.id.digit_9).setOnClickListener(v -> calculator.addDigit("9"));
    }

    @Override
    public void onInvalidateState() {
        numberEditText.setText(calculator.buildOutput());
    }

    @StyleRes
    private static int resolveDialogTheme(@NonNull Context context, @StyleRes int themeResId) {
        if (themeResId == 0) {
            final TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.calculatorDialogTheme, outValue, true);
            return outValue.resourceId;
        } else {
            return themeResId;
        }
    }
}
