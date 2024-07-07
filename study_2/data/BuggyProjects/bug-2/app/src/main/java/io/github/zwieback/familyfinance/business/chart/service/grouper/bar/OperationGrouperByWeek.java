package io.github.zwieback.familyfinance.business.chart.service.grouper.bar;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalUnit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.util.DateUtils;

public class OperationGrouperByWeek extends BarOperationGrouper {

    /**
     * Result.Key - weeks from epoch day.<br/>
     * Result.Value - operations in that week.
     *
     * @param operations source operations
     * @return operations grouped by week
     */
    @Override
    public Map<Float, List<OperationView>> group(List<OperationView> operations,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Map<Float, List<OperationView>> result = new LinkedHashMap<>();
        long weeksBetween = calculatePeriodBetween(startDate, endDate);
        for (long i = 0; i < weeksBetween; i++) {
            LocalDate currentWeek = startDate.plusWeeks(i);
            int week = DateUtils.extractWeekOfYear(currentWeek);
            int year = currentWeek.getYear();
            float weeksFromEpoch = DateUtils.localDateToEpochWeek(currentWeek);
            List<OperationView> weeklyOperations = filterByWeek(year, week, operations);
            result.put(weeksFromEpoch, weeklyOperations);
        }
        return result;
    }

    @Override
    TemporalUnit getTemporalUnit() {
        return ChronoUnit.WEEKS;
    }

    private static List<OperationView> filterByWeek(int year,
                                                    int week,
                                                    List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> operationInWeek(year, week, operation))
                .collect(Collectors.toList());
    }

    private static boolean operationInWeek(int year, int week, OperationView operation) {
        LocalDate operationDate = operation.getDate();
        int operationWeek = DateUtils.extractWeekOfYear(operationDate);
        return year == operationDate.getYear() && week == operationWeek;
    }
}
