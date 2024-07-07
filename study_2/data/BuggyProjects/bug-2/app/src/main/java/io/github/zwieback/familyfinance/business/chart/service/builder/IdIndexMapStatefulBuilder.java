package io.github.zwieback.familyfinance.business.chart.service.builder;

import android.util.Pair;

import com.annimon.stream.Stream;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdIndexMapStatefulBuilder {

    /**
     * key - unique entity id, value - unique bar index (started from 0)
     */
    private Map<Float, Float> idIndexMap;
    private List<Pair<BigDecimal, Float>> sumMap;

    public static IdIndexMapStatefulBuilder create() {
        return new IdIndexMapStatefulBuilder();
    }

    public IdIndexMapStatefulBuilder setSumMap(List<Pair<BigDecimal, Float>> sumMap) {
        this.sumMap = sumMap;
        this.idIndexMap = null;
        return this;
    }

    public Map<Float, Float> build() {
        if (sumMap == null) {
            throw new IllegalStateException("sumMap is not set");
        }
        if (idIndexMap == null) {
            idIndexMap = new HashMap<>();
            Stream.of(sumMap)
                    .sortBy(pair -> pair.first)
                    .forEachIndexed((index, pair) ->
                            idIndexMap.put(pair.second, (float) index));
        }
        return idIndexMap;
    }
}
