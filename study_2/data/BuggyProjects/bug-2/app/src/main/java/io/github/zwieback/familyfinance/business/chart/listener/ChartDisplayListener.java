package io.github.zwieback.familyfinance.business.chart.listener;

import io.github.zwieback.familyfinance.business.chart.display.ChartDisplay;

public interface ChartDisplayListener<D extends ChartDisplay> {

    void onApplyDisplay(D display);
}
