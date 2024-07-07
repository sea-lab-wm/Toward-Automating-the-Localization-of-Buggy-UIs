package io.github.zwieback.familyfinance.calculator.stateful;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.calculator.Calculator;
import io.github.zwieback.familyfinance.calculator.Operator;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.github.zwieback.familyfinance.util.StringUtils;

import static io.github.zwieback.familyfinance.util.StringUtils.EMPTY;

public class StatefulCalculator {

    private String decimalSeparator;
    private String leftOperand;
    private String rightOperand;
    private Operator operator;
    private Calculator calculator;
    private InputState state;
    private OnInvalidateStateListener invalidateListener;

    public StatefulCalculator(@Nullable OnInvalidateStateListener listener,
                              @Nullable BigDecimal defaultValue) {
        calculator = new Calculator();
        decimalSeparator = String.valueOf(NumberUtils.getDecimalSeparator());
        invalidateListener = listener;
        reset();
        leftOperand = NumberUtils.bigDecimalToString(defaultValue);
    }

    public void clear() {
        reset();
        invalidate();
    }

    public void delete() {
        switch (state) {
            case INPUT_LEFT_OPERAND:
                leftOperand = StringUtils.deleteLastChar(leftOperand);
                break;
            case INPUT_RIGHT_OPERAND:
                rightOperand = StringUtils.deleteLastChar(rightOperand);
                if (StringUtils.isTextEmpty(rightOperand)) {
                    state = InputState.INPUT_LEFT_OPERAND;
                    operator = null;
                }
                break;
        }
        invalidate();
    }

    public void eq() {
        updateCalculator();
        reset();
        leftOperand = NumberUtils.bigDecimalToString(calculator.calc());
        invalidate();
    }

    public void changeOperator(Operator operator) {
        this.operator = operator;
        this.state = InputState.INPUT_RIGHT_OPERAND;
        invalidate();
    }

    public void addDecimalSeparator() {
        switch (state) {
            case INPUT_LEFT_OPERAND:
                leftOperand = StringUtils.addUniqueChar(leftOperand, decimalSeparator);
                break;
            case INPUT_RIGHT_OPERAND:
                rightOperand = StringUtils.addUniqueChar(rightOperand, decimalSeparator);
                break;
        }
        invalidate();
    }

    public void addDigit(String digit) {
        switch (state) {
            case INPUT_LEFT_OPERAND:
                leftOperand = StringUtils.addChar(leftOperand, digit);
                break;
            case INPUT_RIGHT_OPERAND:
                rightOperand = StringUtils.addChar(rightOperand, digit);
                break;
        }
        invalidate();
    }

    public void updateCalculator() {
        calculator.setLeftOperand(NumberUtils.stringToBigDecimal(leftOperand));
        calculator.setRightOperand(NumberUtils.stringToBigDecimal(rightOperand));
        calculator.setOperator(operator);
    }

    public String buildOutput() {
        StringBuilder result = new StringBuilder();
        if (StringUtils.isTextNotEmpty(leftOperand)) {
            result.append(leftOperand);
        }
        if (operator != null) {
            result.append(" ").append(operator.opChar).append(" ");
        }
        if (StringUtils.isTextNotEmpty(rightOperand)) {
            result.append(rightOperand);
        }
        return result.toString();
    }

    @Nullable
    public BigDecimal calc() {
        return calculator.calc();
    }

    private void reset() {
        leftOperand = EMPTY;
        rightOperand = EMPTY;
        operator = null;
        state = InputState.INPUT_LEFT_OPERAND;
    }

    private void invalidate() {
        if (invalidateListener != null) {
            invalidateListener.onInvalidateState();
        }
    }
}
