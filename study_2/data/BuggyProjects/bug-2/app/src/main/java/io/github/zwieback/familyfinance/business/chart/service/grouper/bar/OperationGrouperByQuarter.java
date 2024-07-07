package io.github.zwieback.familyfinance.business.chart.service.grouper.bar;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.IsoFields;
import org.threeten.bp.temporal.TemporalUnit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.util.DateUtils;

public class OperationGrouperByQuarter extends BarOperationGrouper {

    /**
     * Result.Key - quarters from epoch day.<br/>
     * Result.Value - operations in that quarter.
     *
     * @param operations source operations
     * @return operations grouped by quarter
     */
    @Override
    public Map<Float, List<OperationView>> group(List<OperationView> operations,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Map<Float, List<OperationView>> result = new LinkedHashMap<>();
        long quartersBetween = calculatePeriodBetween(startDate, endDate);
        for (long i = 0; i < quartersBetween; i++) {
            LocalDate currentQuarter = DateUtils.plusQuarters(startDate, i);
            int quarter = DateUtils.extractQuarterOfYear(currentQuarter);
            int year = currentQuarter.getYear();
            float quartersFromEpoch = DateUtils.localDateToEpochQuarter(currentQuarter);
            List<OperationView> quarterlyOperations = filterByQuarter(year, quarter, operations);
            result.put(quartersFromEpoch, quarterlyOperations);
        }
        return result;
    }

    @Override
    TemporalUnit getTemporalUnit() {
        return IsoFields.QUARTER_YEARS;
    }

    private static List<OperationView> filterByQuarter(int year,
                                                       int quarter,
                                                       List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> operationInQuarter(year, quarter, operation))
                .collect(Collectors.toList());
    }

    private static boolean operationInQuarter(int year, int quarter, OperationView operation) {
        LocalDate operationDate = operation.getDate();
        int operationQuarter = DateUtils.extractQuarterOfYear(operationDate);
        return year == operationDate.getYear() && quarter == operationQuarter;
    }
}
