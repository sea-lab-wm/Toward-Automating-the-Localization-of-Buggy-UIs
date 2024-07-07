package io.github.zwieback.familyfinance.business.chart.service.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.threeten.bp.LocalDate;

import io.github.zwieback.familyfinance.util.DateUtils;

public class WeekValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float weeksFromEpoch, AxisBase axis) {
        LocalDate month = determineCorrectWeek(weeksFromEpoch);
        return DateUtils.localDateToString(month, DateUtils.ISO_LOCAL_WEEK);
    }

    private static LocalDate determineCorrectWeek(float weeksFromEpoch) {
        return DateUtils.epochWeekToLocalDate((long) weeksFromEpoch);
    }
}
