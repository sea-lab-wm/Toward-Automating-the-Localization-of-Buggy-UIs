package io.github.zwieback.familyfinance.business.chart.service.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class YearValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float year, AxisBase axis) {
        return String.valueOf((int) year);
    }
}
