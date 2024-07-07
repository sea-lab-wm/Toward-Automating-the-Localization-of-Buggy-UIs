package io.github.zwieback.familyfinance.business.operation.service.calculator;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.util.ConfigurationUtils;
import io.requery.query.Result;

import static io.github.zwieback.familyfinance.util.NumberUtils.bigDecimalToString;

public final class BalanceCalculator {

    public static String calculateBalance(Result<OperationView> queryResult) {
        Map<CurrencyEntry, List<OperationView>> groupedOperations =
                groupOperationsByCurrency(queryResult);
        Map<CurrencyEntry, BigDecimal> groupedBalance = groupBalanceByCurrency(groupedOperations);
        List<String> formattedBalance = formatBalance(groupedBalance);
        return joinBalance(formattedBalance);
    }

    private static Map<CurrencyEntry, List<OperationView>>
    groupOperationsByCurrency(Result<OperationView> queryResult) {
        return Stream.of(queryResult.toList())
                .collect(Collectors.groupingBy(CurrencyEntry::new));
    }

    private static Map<CurrencyEntry, BigDecimal>
    groupBalanceByCurrency(Map<CurrencyEntry, List<OperationView>> groupedOperations) {
        return Stream.of(groupedOperations)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> calculateBalance(entry.getValue())));
    }

    private static BigDecimal calculateBalance(List<OperationView> operations) {
        BigDecimal incomeBalance =
                calculateBalanceByType(operations, BalanceCalculator::isIncomeOperation);
        BigDecimal expenseBalance =
                calculateBalanceByType(operations, BalanceCalculator::isExpenseOperation);
        return incomeBalance.subtract(expenseBalance);
    }

    private static BigDecimal calculateBalanceByType(List<OperationView> operations,
                                                     Predicate<OperationView> typePredicate) {
        return Stream.of(operations)
                .filter(typePredicate)
                .map(OperationView::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static boolean isIncomeOperation(OperationView operation) {
        return OperationType.getIncomeTypes().contains(operation.getType());
    }

    private static boolean isExpenseOperation(OperationView operation) {
        return OperationType.getExpenseTypes().contains(operation.getType());
    }

    private static List<String> formatBalance(Map<CurrencyEntry, BigDecimal> groupedBalance) {
        Locale locale = ConfigurationUtils.getSystemLocale();
        return Stream.of(groupedBalance)
                .map(balance ->
                        String.format(locale, "%s %s",
                                bigDecimalToString(balance.getValue()),
                                balance.getKey().name))
                .collect(Collectors.toList());
    }

    private static String joinBalance(List<String> formattedBalance) {
        if (formattedBalance.isEmpty()) {
            return "0";
        }
        return Stream.of(formattedBalance).collect(Collectors.joining("; "));
    }
}
