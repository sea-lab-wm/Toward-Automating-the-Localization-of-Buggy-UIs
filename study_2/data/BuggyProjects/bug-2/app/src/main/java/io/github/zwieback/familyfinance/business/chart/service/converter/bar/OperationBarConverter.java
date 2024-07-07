package io.github.zwieback.familyfinance.business.chart.service.converter.bar;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationSumConverter;
import io.github.zwieback.familyfinance.core.model.OperationView;

public class OperationBarConverter implements OperationConverter<BarEntry> {

    private final OperationSumConverter sumConverter;

    public OperationBarConverter(@NonNull Context context) {
        this.sumConverter = new OperationSumConverter(context);
    }

    @Override
    public List<BarEntry> convertToEntries(Map<Float, List<OperationView>> operations) {
        Map<Float, BigDecimal> sumMap = sumConverter.convertToSumMap(operations);
        return Stream.of(sumMap)
                .map(operationEntry -> {
                    // period depends on the implementation of the BarOperationGrouper
                    // it may be day, week, month, etc.
                    Float periodOfOperations = operationEntry.getKey();
                    float sumOfOperations = operationEntry.getValue().floatValue();
                    return new BarEntry(periodOfOperations, sumOfOperations);
                })
                .sortBy(Entry::getX)
                .collect(Collectors.toList());
    }
}
