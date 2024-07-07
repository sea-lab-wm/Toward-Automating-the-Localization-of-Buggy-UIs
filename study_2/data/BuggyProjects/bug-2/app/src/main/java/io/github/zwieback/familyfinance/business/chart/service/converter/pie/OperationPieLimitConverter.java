package io.github.zwieback.familyfinance.business.chart.service.converter.pie;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.display.type.PieChartGroupByType;
import io.github.zwieback.familyfinance.core.model.OperationView;

public class OperationPieLimitConverter extends OperationPieSimpleConverter {

    private static final BigDecimal MAX_PERCENT = BigDecimal.valueOf(100);

    private final String otherGroupName;
    private final int minPercent;

    public OperationPieLimitConverter(@NonNull Context context, PieChartGroupByType groupByType) {
        super(context, groupByType);
        this.minPercent = context.getResources().getInteger(R.integer.pie_min_percent);
        this.otherGroupName = context.getString(R.string.pie_group_other);
    }

    @Override
    public List<PieEntry> convertToEntries(Map<Float, List<OperationView>> operations) {
        Map<String, BigDecimal> sumMap = convertToSumMap(operations);
        Map<String, BigDecimal> reorderedSumMap = reorderSumMap(sumMap);
        return Stream.of(reorderedSumMap)
                .map(OperationPieSimpleConverter::convertToEntry)
                .sortBy(PieEntry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Reorder the card as follows: those sums that are less than the minimum
     * percentage will be placed in a separate group, the rest will not be
     * changed.
     *
     * @param sumMap source map calculated in the {@link #convertToSumMap}
     * @return reordered map
     */
    private Map<String, BigDecimal> reorderSumMap(Map<String, BigDecimal> sumMap) {
        BigDecimal totalSum = calculateTotalSum(sumMap);
        BigDecimal minSum = calculateMinSum(totalSum);
        BigDecimal otherSum = Stream.of(sumMap)
                .filter(entry -> entry.getValue().compareTo(minSum) <= 0)
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (otherSum.equals(BigDecimal.ZERO)) {
            return sumMap;
        }
        Map<String, BigDecimal> reorderedMap = Stream.of(sumMap)
                .filter(entry -> entry.getValue().compareTo(minSum) > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        reorderedMap.put(otherGroupName, otherSum);
        return reorderedMap;
    }

    private BigDecimal calculateMinSum(BigDecimal totalSum) {
        BigDecimal minPercentDecimal = BigDecimal.valueOf(minPercent);
        return totalSum.multiply(minPercentDecimal).divide(MAX_PERCENT, RoundingMode.DOWN);
    }

    private BigDecimal calculateTotalSum(Map<String, BigDecimal> sumMap) {
        return Stream.of(sumMap)
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
