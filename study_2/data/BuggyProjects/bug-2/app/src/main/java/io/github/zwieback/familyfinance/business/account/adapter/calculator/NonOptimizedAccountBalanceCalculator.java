package io.github.zwieback.familyfinance.business.account.adapter.calculator;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.github.zwieback.familyfinance.core.model.AccountView;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.util.BigDecimalConverterUtils;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.query.NamedNumericExpression;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * Non-optimized calculator, but without loss of precision.
 */
public class NonOptimizedAccountBalanceCalculator extends AccountBalanceCalculator {

    public NonOptimizedAccountBalanceCalculator(ReactiveEntityStore<Persistable> data, AccountView account) {
        super(data, account);
    }

    @Override
    public void calculateBalance(Consumer<BigDecimal> showBalanceConsumer) {
        int accountId = account.getId();
        int currencyId = account.getCurrencyId();

        BigDecimal nativeIncomes = calculateSumInNativeCurrency(accountId, currencyId,
                OperationType.getIncomeTypes());
        BigDecimal nativeExpenses = calculateSumInNativeCurrency(accountId, currencyId,
                OperationType.getExpenseTypes());

        BigDecimal balance = account.getInitialBalance()
                .add(nativeIncomes)
                .subtract(nativeExpenses);

        if (accountHasOperationsInForeignCurrency(accountId, currencyId)) {
            BigDecimal foreignIncomes = calculateSumInForeignCurrency(accountId, currencyId,
                    OperationType.getIncomeTypes());
            BigDecimal foreignExpenses = calculateSumInForeignCurrency(accountId, currencyId,
                    OperationType.getExpenseTypes());

            balance = balance
                    .add(foreignIncomes)
                    .subtract(foreignExpenses);
        }

        try {
            showBalanceConsumer.accept(balance);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private boolean accountHasOperationsInForeignCurrency(int accountId, int nativeCurrencyId) {
        List<ExchangeRate> exchangeRatesNotInNativeCurrency = data
                .select(ExchangeRate.class)
                .join(Operation.class)
                .on(ExchangeRate.ID.eq(Operation.EXCHANGE_RATE_ID))
                .where(ExchangeRate.CURRENCY_ID.notEqual(nativeCurrencyId)
                        .and(Operation.ACCOUNT_ID.equal(accountId))
                )
                .get().toList();
        return !exchangeRatesNotInNativeCurrency.isEmpty();
    }

    private BigDecimal calculateSumInNativeCurrency(int accountId,
                                                    int currencyId,
                                                    List<OperationType> types) {
        Long sumInNativeCurrency = data
                .select(NamedNumericExpression.ofLong("t_native.value").sum()
                        .as("native_sum"))
                .from(data
                        .select(Operation.VALUE.as("value"))
                        .join(ExchangeRate.class)
                        .on(ExchangeRate.CURRENCY_ID.eq(currencyId))
                        .and(ExchangeRate.ID.eq(Operation.EXCHANGE_RATE_ID))
                        .where(Operation.TYPE.in(types)
                                .and(Operation.ACCOUNT_ID.eq(accountId)))
                        .as("t_native")
                )
                .get().first().get("native_sum");

        if (sumInNativeCurrency == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimalConverterUtils.worthToBigDecimal(sumInNativeCurrency);
    }

    // TODO rewrite this method after the issue https://github.com/requery/requery/issues/593 is closed
    // todo before change the calculation depending on IExchangeRate.getRelatedToCurrency() - ICurrency
    private BigDecimal calculateSumInForeignCurrency(int accountId,
                                                     int currencyId,
                                                     List<OperationType> types) {
        List<ExchangeRate> exchangeRates = data
                .select(ExchangeRate.class)
                .join(Operation.class)
                .on(ExchangeRate.ID.eq(Operation.EXCHANGE_RATE_ID))
                .where(ExchangeRate.CURRENCY_ID.notEqual(currencyId)
                        .and(Operation.ACCOUNT_ID.equal(accountId)
                                .and(Operation.TYPE.in(types)))
                )
                .get().toList();

        Map<ExchangeRate, BigDecimal> sumOfOperationsByExchangeRates =
                calculateSumByExchangeRates(accountId, types, exchangeRates);

        return calculateTotalProduct(sumOfOperationsByExchangeRates);
    }

    private Map<ExchangeRate, BigDecimal>
    calculateSumByExchangeRates(int accountId,
                                List<OperationType> types,
                                List<ExchangeRate> exchangeRates) {
        return Stream.of(exchangeRates)
                .collect(Collectors.toMap(
                        entry -> entry,
                        entry -> calculateSumByExchangeRate(accountId, types, entry)));
    }

    private BigDecimal calculateSumByExchangeRate(int accountId,
                                                  List<OperationType> types,
                                                  ExchangeRate exchangeRate) {
        Long sumInForeignCurrency = data
                .select(NamedNumericExpression.ofLong("t_foreign.value").sum()
                        .as("foreign_sum"))
                .from(data
                        .select(Operation.VALUE.as("value"))
                        .where(Operation.TYPE.in(types)
                                .and(Operation.ACCOUNT_ID.eq(accountId))
                                .and(Operation.EXCHANGE_RATE_ID.eq(exchangeRate.getId())))
                        .as("t_foreign")
                )
                .get().first().get("foreign_sum");

        if (sumInForeignCurrency == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimalConverterUtils.worthToBigDecimal(sumInForeignCurrency);
    }

    private static BigDecimal
    calculateTotalProduct(Map<ExchangeRate, BigDecimal> sumOfOperationsByExchangeRates) {
        return Stream.of(sumOfOperationsByExchangeRates)
                .map(calculateProductOfExchangeRateAndSum())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Function<Map.Entry<ExchangeRate, BigDecimal>, BigDecimal>
    calculateProductOfExchangeRateAndSum() {
        return entry -> {
            ExchangeRate exchangeRate = entry.getKey();
            BigDecimal sum = entry.getValue();
            return exchangeRate.getValue().multiply(sum);
        };
    }
}
