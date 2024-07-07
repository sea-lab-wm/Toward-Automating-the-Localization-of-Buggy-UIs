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

public class OperationGrouperByMonth extends BarOperationGrouper {

    /**
     * Result.Key - days from epoch day.<br/>
     * Result.Value - operations in that month.
     *
     * @param operations source operations
     * @return operations grouped by month
     */
    @Override
    public Map<Float, List<OperationView>> group(List<OperationView> operations,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Map<Float, List<OperationView>> result = new LinkedHashMap<>();
        long monthsBetween = calculatePeriodBetween(startDate, endDate);
        for (long i = 0; i < monthsBetween; i++) {
            LocalDate currentMonth = startDate.plusMonths(i);
            int month = currentMonth.getMonthValue();
            int year = currentMonth.getYear();
            float monthsFromEpoch = DateUtils.localDateToEpochMonth(currentMonth);
            List<OperationView> monthlyOperations = filterByMonth(year, month, operations);
            result.put(monthsFromEpoch, monthlyOperations);
        }
        return result;
    }

    @Override
    TemporalUnit getTemporalUnit() {
        return ChronoUnit.MONTHS;
    }

    private static List<OperationView> filterByMonth(int year,
                                                     int month,
                                                     List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> operationInMonth(year, month, operation))
                .collect(Collectors.toList());
    }

    private static boolean operationInMonth(int year, int month, OperationView operation) {
        LocalDate operationDate = operation.getDate();
        return year == operationDate.getYear() && month == operationDate.getMonthValue();
    }
}
