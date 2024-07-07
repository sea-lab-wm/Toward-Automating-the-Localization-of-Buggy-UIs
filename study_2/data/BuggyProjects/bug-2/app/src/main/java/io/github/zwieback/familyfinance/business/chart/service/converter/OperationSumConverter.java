package io.github.zwieback.familyfinance.business.chart.service.converter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.business.operation.service.calculator.OperationCalculator;
import io.github.zwieback.familyfinance.core.model.OperationView;

public class OperationSumConverter {

    private final OperationCalculator calculator;

    public OperationSumConverter(@NonNull Context context) {
        this.calculator = new OperationCalculator(context);
    }

    /**
     * Calculate and return map with sum of operations.
     *
     * @param operations source operations
     * @return key - same key as input key, value - sum of operations list
     */
    public Map<Float, BigDecimal> convertToSumMap(Map<Float, List<OperationView>> operations) {
        return Stream.of(operations)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculator.calculateSum(entry.getValue())));
    }
}
