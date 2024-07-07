package io.github.zwieback.familyfinance.business.chart.service.grouper.pie;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Set;

import io.github.zwieback.familyfinance.core.model.OperationView;

public class OperationGrouperByArticleParent extends PieOperationGrouper {

    /**
     * Collect article parent ids without {@code null} (root) operations.
     *
     * @param operations source operations
     * @return set of article parent ids
     */
    @Override
    Set<Integer> collectGroupIds(List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> operation.getArticleParentId() != null)
                .map(OperationView::getArticleParentId)
                .collect(Collectors.toSet());
    }

    @Override
    List<OperationView> filterByGroup(@NonNull Integer articleParentId,
                                      List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> articleParentId.equals(operation.getArticleParentId()))
                .collect(Collectors.toList());
    }
}
