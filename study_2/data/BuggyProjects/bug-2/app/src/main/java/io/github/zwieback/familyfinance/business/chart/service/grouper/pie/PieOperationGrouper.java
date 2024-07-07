package io.github.zwieback.familyfinance.business.chart.service.grouper.pie;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.threeten.bp.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.zwieback.familyfinance.business.chart.service.grouper.OperationGrouper;
import io.github.zwieback.familyfinance.core.model.OperationView;

abstract class PieOperationGrouper implements OperationGrouper {

    /**
     * Result.Key - group id.<br/>
     * Result.Value - operations of that group.
     *
     * @param operations source operations
     * @return grouped operations
     */
    @Override
    public Map<Float, List<OperationView>> group(List<OperationView> operations,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        Set<Integer> groupIds = collectGroupIds(operations);
        return Stream.of(groupIds)
                .collect(Collectors.toMap(
                        Float::valueOf,
                        groupId -> filterByGroup(groupId, operations)
                ));
    }

    /**
     * Collect article ids.
     *
     * @param operations source operations
     * @return set of article ids
     */
    abstract Set<Integer> collectGroupIds(List<OperationView> operations);

    abstract List<OperationView> filterByGroup(@NonNull Integer groupId,
                                               List<OperationView> operations);
}
