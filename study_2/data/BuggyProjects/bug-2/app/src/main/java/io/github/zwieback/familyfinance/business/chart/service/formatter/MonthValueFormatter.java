package io.github.zwieback.familyfinance.business.chart.service.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.threeten.bp.LocalDate;

import io.github.zwieback.familyfinance.util.DateUtils;

public class MonthValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float monthsFromEpoch, AxisBase axis) {
        LocalDate month = determineCorrectMonth(monthsFromEpoch);
        return DateUtils.localDateToString(month, DateUtils.ISO_LOCAL_MONTH);
    }

    private static LocalDate determineCorrectMonth(float monthsFromEpoch) {
        return DateUtils.epochMonthToLocalDate((long) monthsFromEpoch);
    }
}
