package io.github.zwieback.familyfinance.business.chart.service.converter.pie;

import android.content.Context;
import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.github.mikephil.charting.data.PieEntry;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.business.chart.display.type.PieChartGroupByType;
import io.github.zwieback.familyfinance.business.chart.exception.UnsupportedPieChartGroupByTypeException;
import io.github.zwieback.familyfinance.business.chart.service.converter.OperationConverter;
import io.github.zwieback.familyfinance.business.operation.service.calculator.OperationCalculator;
import io.github.zwieback.familyfinance.core.model.OperationView;

public class OperationPieSimpleConverter implements OperationConverter<PieEntry> {

    private final OperationCalculator calculator;
    private final PieChartGroupByType groupByType;

    public OperationPieSimpleConverter(@NonNull Context context, PieChartGroupByType groupByType) {
        this.calculator = new OperationCalculator(context);
        this.groupByType = groupByType;
    }

    @Override
    public List<PieEntry> convertToEntries(Map<Float, List<OperationView>> operations) {
        Map<String, BigDecimal> sumMap = convertToSumMap(operations);
        return Stream.of(sumMap)
                .map(OperationPieSimpleConverter::convertToEntry)
                .sortBy(PieEntry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Result.Key - article (parent) name.<br/>
     * Result.Value - calculated sum of source operations.
     *
     * @param operations source operations
     * @return converted map
     * @implNote Each group contains at least one operation. This is guaranteed
     * by {@link io.github.zwieback.familyfinance.business.chart.service.grouper.pie.OperationGrouperByArticle}
     * and {@link io.github.zwieback.familyfinance.business.chart.service.grouper.pie.OperationGrouperByArticleParent}
     */
    Map<String, BigDecimal> convertToSumMap(Map<Float, List<OperationView>> operations) {
        return Stream.of(operations)
                .collect(Collectors.toMap(
                        entry -> determineGroupName(entry.getValue()),
                        entry -> calculator.calculateSum(entry.getValue())));
    }

    /**
     * @param operations source operations
     * @return group name of operations
     */
    @NonNull
    private String determineGroupName(List<OperationView> operations) {
        switch (groupByType) {
            case ARTICLE:
                return operations.get(0).getArticleName();
            case ARTICLE_PARENT:
                return operations.get(0).getArticleParentName();
        }
        throw new UnsupportedPieChartGroupByTypeException();
    }

    static PieEntry convertToEntry(Map.Entry<String, BigDecimal> entry) {
        float sumOfOperations = entry.getValue().floatValue();
        String groupName = entry.getKey();
        return new PieEntry(sumOfOperations, groupName);
    }
}
