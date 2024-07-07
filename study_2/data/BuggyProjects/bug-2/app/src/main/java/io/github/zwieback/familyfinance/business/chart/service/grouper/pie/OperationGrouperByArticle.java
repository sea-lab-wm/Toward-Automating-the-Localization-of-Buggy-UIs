package io.github.zwieback.familyfinance.business.chart.service.grouper.pie;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;
import java.util.Set;

import io.github.zwieback.familyfinance.core.model.OperationView;

public class OperationGrouperByArticle extends PieOperationGrouper {

    /**
     * Collect article ids.
     *
     * @param operations source operations
     * @return set of article ids
     */
    @Override
    Set<Integer> collectGroupIds(List<OperationView> operations) {
        return Stream.of(operations)
                .map(OperationView::getArticleId)
                .collect(Collectors.toSet());
    }

    @Override
    List<OperationView> filterByGroup(@NonNull Integer articleId,
                                      List<OperationView> operations) {
        return Stream.of(operations)
                .filter(operation -> articleId.equals(operation.getArticleId()))
                .collect(Collectors.toList());
    }
}
