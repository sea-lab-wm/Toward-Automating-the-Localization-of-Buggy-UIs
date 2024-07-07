package io.github.zwieback.familyfinance.calculator;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

public class Calculator {

    @Nullable
    private BigDecimal leftOperand;
    @Nullable
    private BigDecimal rightOperand;
    @Nullable
    private Operator operator;

    @Nullable
    public BigDecimal calc() {
        if (operator == null || rightOperand == null) {
            return leftOperand;
        }
        if (leftOperand == null) {
            leftOperand = BigDecimal.ZERO;
        }
        try {
            return operator.calc(leftOperand, rightOperand);
        } catch (ArithmeticException ignored) {
            return null;
        }
    }

    public void setLeftOperand(@Nullable BigDecimal leftOperand) {
        this.leftOperand = leftOperand;
    }

    public void setRightOperand(@Nullable BigDecimal rightOperand) {
        this.rightOperand = rightOperand;
    }

    public void setOperator(@Nullable Operator operator) {
        this.operator = operator;
    }
}
