package io.github.zwieback.familyfinance.business.chart.service.converter;

import com.github.mikephil.charting.data.Entry;

import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.OperationView;

public interface OperationConverter<E extends Entry> {

    /**
     * Convert operations into the list of {@link Entry}.
     *
     * @param groupedOperations operations that grouped by one of the {@link io.github.zwieback.familyfinance.business.chart.service.grouper.OperationGrouper}
     * @return list of entries to display in chart
     * @implNote sorting is extremely important!
     * @see <a href="https://github.com/PhilJay/MPAndroidChart/issues/983#issuecomment-152299035">
     * setVisibleXRangeMaximum not working as expected</a>
     * @see <a href="https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data#the-order-of-entries">
     * The order of entries</a>
     */
    List<E> convertToEntries(Map<Float, List<OperationView>> groupedOperations);
}
