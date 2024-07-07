package io.github.zwieback.familyfinance.business.chart.service.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.threeten.bp.LocalDate;

import io.github.zwieback.familyfinance.util.DateUtils;

public class DayValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float daysFromEpoch, AxisBase axis) {
        LocalDate day = determineCorrectDay(daysFromEpoch);
        return DateUtils.localDateToString(day);
    }

    private static LocalDate determineCorrectDay(float daysFromEpoch) {
        return DateUtils.epochDayToLocalDate((long) daysFromEpoch);
    }
}
