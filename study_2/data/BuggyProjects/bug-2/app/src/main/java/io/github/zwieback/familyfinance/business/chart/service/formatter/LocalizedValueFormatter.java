package io.github.zwieback.familyfinance.business.chart.service.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.util.NumberUtils;

public class LocalizedValueFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return NumberUtils.bigDecimalToString(BigDecimal.valueOf(value),
                NumberUtils.ACCOUNT_PLACES);
    }
}
