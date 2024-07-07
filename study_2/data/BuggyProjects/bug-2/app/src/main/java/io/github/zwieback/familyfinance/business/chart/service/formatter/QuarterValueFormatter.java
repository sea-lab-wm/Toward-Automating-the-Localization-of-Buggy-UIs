package io.github.zwieback.familyfinance.business.chart.service.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.threeten.bp.LocalDate;

import io.github.zwieback.familyfinance.util.DateUtils;

public class QuarterValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float quartersFromEpoch, AxisBase axis) {
        LocalDate quarter = determineCorrectQuarter(quartersFromEpoch);
        return DateUtils.localDateToString(quarter, DateUtils.ISO_LOCAL_QUARTER);
    }

    private static LocalDate determineCorrectQuarter(float quartersFromEpoch) {
        return DateUtils.epochQuarterToLocalDate((long) quartersFromEpoch);
    }
}
