package io.github.zwieback.familyfinance.business.chart.service.grouper;

import org.threeten.bp.LocalDate;

import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.OperationView;

public interface OperationGrouper {

    Map<Float, List<OperationView>> group(List<OperationView> operations,
                                          LocalDate startDate,
                                          LocalDate endDate);
}
