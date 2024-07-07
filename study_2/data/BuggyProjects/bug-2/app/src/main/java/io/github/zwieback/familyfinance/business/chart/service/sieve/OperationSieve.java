package io.github.zwieback.familyfinance.business.chart.service.sieve;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;

public class OperationSieve {

    public Map<Float, List<OperationView>> filterByTypes(Map<Float, List<OperationView>> operations,
                                                         List<OperationType> types) {
        return Stream.of(operations)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> internalFilterByType(entry.getValue(), types)));
    }

    private static List<OperationView> internalFilterByType(List<OperationView> operations,
                                                            List<OperationType> types) {
        return Stream.of(operations)
                .filter(operation -> types.contains(operation.getType()))
                .collect(Collectors.toList());
    }
}
