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

public class OperationGrouperByYear extends BarOperationGrouper {

    /**
     * Result.Key - year.<br/>
     * Result.Value - operations in that year.
     *
     * @param operations source operations
     * @return operations grouped by year
     */
    @Override
    public Map<Float, List<OperationView>> group(List<OperationView> operations,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Map<Float, List<OperationView>> result = new LinkedHashMap<>();
        long yearsBetween = calculatePeriodBetween(startDate, endDate);
        for (long i = 0; i < yearsBetween; i++) {
            int currentYear = startDate.plusYears(i).getYear();
            List<OperationView> yearlyOperations = filterByYear(currentYear, operations);
            result.put((float) currentYear, yearlyOperations);
        }
        return result;
    }

    @Override
    TemporalUnit getTemporalUnit() {
        return ChronoUnit.YEARS;
    }

    private static List<OperationView> filterByYear(int year, List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> year == operation.getDate().getYear())
                .collect(Collectors.toList());
    }
}
