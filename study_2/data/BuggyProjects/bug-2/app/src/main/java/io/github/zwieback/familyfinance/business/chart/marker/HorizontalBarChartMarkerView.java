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
public class HorizontalBarChartMarkerView extends MarkerView {

    private TextView labelContent;
    private TextView valueContent;
    private IAxisValueFormatter xAxisValueFormatter;
    private IAxisValueFormatter yAxisValueFormatter;

    public HorizontalBarChartMarkerView(@NonNull Context context,
                                        @NonNull IAxisValueFormatter xAxisValueFormatter,
                                        @NonNull IAxisValueFormatter yAxisValueFormatter) {
        super(context, R.layout.chart_pie_marker_view);
        this.xAxisValueFormatter = xAxisValueFormatter;
        this.yAxisValueFormatter = yAxisValueFormatter;
        this.labelContent = findViewById(R.id.label_content);
        this.valueContent = findViewById(R.id.value_content);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        labelContent.setText(xAxisValueFormatter.getFormattedValue(e.getX(), null));
        valueContent.setText(yAxisValueFormatter.getFormattedValue(e.getY(), null));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(0, -getHeight());
    }
}
