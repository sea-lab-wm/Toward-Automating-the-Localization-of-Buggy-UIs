package io.github.zwieback.familyfinance.business.operation.service.calculator;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Stream;

import java.math.BigDecimal;
import java.util.List;

import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;

public class OperationCalculator {

    private final DatabasePrefs databasePrefs;

    public OperationCalculator(@NonNull Context context) {
        databasePrefs = DatabasePrefs.with(context);
    }

    @NonNull
    public BigDecimal calculateSum(List<OperationView> operations) {
        int nativeCurrencyId = databasePrefs.getCurrencyId();
        BigDecimal nativeSum = calculateSumInNativeCurrency(nativeCurrencyId, operations);
        BigDecimal foreignSum = calculateSumInForeignCurrency(nativeCurrencyId, operations);
        return nativeSum.add(foreignSum);
    }

    @NonNull
    private static BigDecimal calculateSumInNativeCurrency(int nativeCurrencyId,
                                                           List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> nativeCurrencyId == operation.getCurrencyId())
                .map(OperationView::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @NonNull
    private static BigDecimal calculateSumInForeignCurrency(int nativeCurrencyId,
                                                            List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> nativeCurrencyId != operation.getCurrencyId())
                .map(operation -> operation.getValue().multiply(operation.getExchangeRateValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
