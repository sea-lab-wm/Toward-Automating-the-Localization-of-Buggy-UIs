package io.github.zwieback.familyfinance.business.chart.marker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import io.github.zwieback.familyfinance.R;

@SuppressLint("ViewConstructor")
public class BarChartMarkerView extends MarkerView {

    private TextView markerContent;
    private IAxisValueFormatter xAxisValueFormatter;
    private IAxisValueFormatter yAxisValueFormatter;

    public BarChartMarkerView(@NonNull Context context,
                              @NonNull IAxisValueFormatter xAxisValueFormatter,
                              @NonNull IAxisValueFormatter yAxisValueFormatter) {
        super(context, R.layout.chart_bar_marker_view);
        this.xAxisValueFormatter = xAxisValueFormatter;
        this.yAxisValueFormatter = yAxisValueFormatter;
        this.markerContent = findViewById(R.id.marker_content);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        markerContent.setText("x: " + xAxisValueFormatter.getFormattedValue(e.getX(), null) +
                "; y: " + yAxisValueFormatter.getFormattedValue(e.getY(), null));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
