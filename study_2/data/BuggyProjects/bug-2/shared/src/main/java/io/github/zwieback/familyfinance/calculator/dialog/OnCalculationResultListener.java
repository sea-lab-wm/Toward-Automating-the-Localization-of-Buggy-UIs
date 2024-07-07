package io.github.zwieback.familyfinance.calculator.dialog;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

public interface OnCalculationResultListener {

    void onCalculationResult(@Nullable BigDecimal result);
}
