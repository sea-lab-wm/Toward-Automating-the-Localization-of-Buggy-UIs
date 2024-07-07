package io.github.zwieback.familyfinance.calculator;

import android.support.annotation.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum Operator {

    ADD("+") {
        @NonNull
        @Override
        public BigDecimal calc(@NonNull BigDecimal leftOperand, @NonNull BigDecimal rightOperand) {
            return leftOperand.add(rightOperand);
        }
    },
    SUB("-") {
        @NonNull
        @Override
        public BigDecimal calc(@NonNull BigDecimal leftOperand, @NonNull BigDecimal rightOperand) {
            return leftOperand.subtract(rightOperand);
        }
    },
    MUL("×") {
        @NonNull
        @Override
        public BigDecimal calc(@NonNull BigDecimal leftOperand, @NonNull BigDecimal rightOperand) {
            return leftOperand.multiply(rightOperand);
        }
    },
    DIV("÷") {
        @NonNull
        @Override
        public BigDecimal calc(@NonNull BigDecimal leftOperand, @NonNull BigDecimal rightOperand) {
            return leftOperand.divide(rightOperand, RoundingMode.HALF_EVEN);
        }
    };

    public final String opChar;

    Operator(String opChar) {
        this.opChar = opChar;
    }

    @NonNull
    public abstract BigDecimal calc(@NonNull BigDecimal leftOperand,
                                    @NonNull BigDecimal rightOperand);
}
