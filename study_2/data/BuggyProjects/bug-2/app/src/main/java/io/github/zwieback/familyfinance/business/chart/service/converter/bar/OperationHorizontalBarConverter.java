package io.github.zwieback.familyfinance.business.chart.service.converter.bar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.business.chart.service.builder.IdIndexMapStatefulBuilder;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationSumConverter;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.util.CollectionUtils;

public class OperationHorizontalBarConverter implements OperationConverter<BarEntry> {

    final OperationSumConverter sumConverter;
    final IdIndexMapStatefulBuilder builder;

    public OperationHorizontalBarConverter(@NonNull Context context,
                                           @NonNull IdIndexMapStatefulBuilder builder) {
        this.sumConverter = new OperationSumConverter(context);
        this.builder = builder;
    }

    /**
     * Result.X - bar index.<br/>
     * Result.Y - sum of operations.
     *
     * @param operations key - entity id, value - list of operations
     * @return list of entries to display in horizontal bar chart
     */
    @Override
    public List<BarEntry> convertToEntries(Map<Float, List<OperationView>> operations) {
        Map<Float, BigDecimal> sumMap = sumConverter.convertToSumMap(operations);
        List<Pair<BigDecimal, Float>> swappedSumMap = CollectionUtils.swapMap(sumMap);
        Map<Float, Float> idIndexMap = builder.setSumMap(swappedSumMap).build();

        return Stream.of(swappedSumMap)
                .map(entry -> {
                    Float barIndex = idIndexMap.get(entry.second);
                    Float sumOfOperations = entry.first.floatValue();
                    return new BarEntry(barIndex, sumOfOperations);
                })
                .sortBy(Entry::getX)
                .collect(Collectors.toList());
    }
}
