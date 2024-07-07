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

public class OperationGrouperByDay extends BarOperationGrouper {

    /**
     * Result.Key - days from epoch day.<br/>
     * Result.Value - operations is that day.
     *
     * @param operations source operations
     * @return operations grouped by day
     */
    @Override
    public Map<Float, List<OperationView>> group(List<OperationView> operations,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Map<Float, List<OperationView>> result = new LinkedHashMap<>();
        long daysBetween = calculatePeriodBetween(startDate, endDate);
        for (long i = 0; i < daysBetween; i++) {
            LocalDate currentDay = startDate.plusDays(i);
            float daysFromEpoch = DateUtils.localDateToEpochDay(currentDay);
            List<OperationView> dailyOperations = filterByDay(currentDay, operations);
            result.put(daysFromEpoch, dailyOperations);
        }
        return result;
    }

    @Override
    TemporalUnit getTemporalUnit() {
        return ChronoUnit.DAYS;
    }

    private static List<OperationView> filterByDay(LocalDate date, List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> date.equals(operation.getDate()))
                .collect(Collectors.toList());
    }
}
