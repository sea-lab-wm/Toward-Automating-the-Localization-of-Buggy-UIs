package io.github.zwieback.familyfinance.business.account.adapter.calculator;

import com.annimon.stream.function.Function;

import java.math.BigDecimal;
import java.util.List;

import io.github.zwieback.familyfinance.core.model.AccountView;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.util.BigDecimalConverterUtils;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.query.Tuple;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveResult;

/**
 * The optimized calculator, but with loss of precision.
 * <p>
 * Use {@link NonOptimizedAccountBalanceCalculator} instead.
 */
@Deprecated
public class DeprecatedOptimizedAccountBalanceCalculator extends AccountBalanceCalculator {

    public DeprecatedOptimizedAccountBalanceCalculator(ReactiveEntityStore<Persistable> data, AccountView account) {
        super(data, account);
    }

    @Override
    public void calculateBalance(Consumer<BigDecimal> showBalanceConsumer) {
        String query = "select" +
                "  current_account.initial_balance + (incomes.sum_value - expenses.sum_value)" +
                "    as 'balance_in_native_currency'," +
                "  cast(foreign_incomes.sum_value - foreign_expenses.sum_value as float)" +
                "    as 'balance_in_foreign_currency'" +
                " from" +
                "  (" +
                "    select coalesce(sum(incomes._value), 0) as sum_value" +
                "      from operation as incomes" +
                "           inner join exchange_rate er on incomes.exchange_rate_id = er.id" +
                "     where" +
                "           incomes._type in ?" +
                "       and incomes.account_id = :accountId" +
                "       and er.currency_id = :currencyId" +
                "  ) as incomes," +
                "  (" +
                "    select coalesce(sum(expenses._value), 0) as sum_value" +
                "      from operation as expenses" +
                "           inner join exchange_rate er on expenses.exchange_rate_id = er.id" +
                "     where" +
                "           expenses._type in ?" +
                "       and expenses.account_id = :accountId" +
                "       and er.currency_id = :currencyId" +
                "  ) as expenses," +
                "  (" +
                "    select coalesce(acc.initial_balance, 0) as initial_balance" +
                "      from account as acc" +
                "     where acc.id = :accountId" +
                "  ) as current_account," +
                "  (" +
                "    select coalesce(sum(incomes._value * er._value), 0) as sum_value" +
                "      from operation as incomes" +
                "           inner join exchange_rate er on incomes.exchange_rate_id = er.id" +
                "     where" +
                "           incomes._type in ?" +
                "       and incomes.account_id = :accountId" +
                "       and er.currency_id <> :currencyId" +
                "  ) as foreign_incomes," +
                "  (" +
                "    select coalesce(sum(expenses._value * er._value), 0) as sum_value" +
                "      from operation as expenses" +
                "           inner join exchange_rate er on expenses.exchange_rate_id = er.id" +
                "     where" +
                "           expenses._type in ?" +
                "       and expenses.account_id = :accountId" +
                "       and er.currency_id <> :currencyId" +
                "  ) as foreign_expenses" +
                ";";

        int accountId = account.getId();
        int currencyId = account.getCurrencyId();
        query = query.replaceAll(":accountId", String.valueOf(accountId));
        query = query.replaceAll(":currencyId", String.valueOf(currencyId));
        List<OperationType> incomeTypes = OperationType.getIncomeTypes();
        List<OperationType> expenseTypes = OperationType.getExpenseTypes();
        Object[] parameters = {incomeTypes, expenseTypes, incomeTypes, expenseTypes};
        ReactiveResult<Tuple> result = data.raw(query, parameters);

        result.observable().subscribe(tuple -> {
            BigDecimal balanceInNativeCurrency = extractBalance(tuple,
                    "balance_in_native_currency",
                    BigDecimalConverterUtils::balanceInNativeCurrencyToBigDecimal);
            BigDecimal balanceInForeignCurrency = extractBalance(tuple,
                    "balance_in_foreign_currency",
                    BigDecimalConverterUtils::balanceInForeignCurrencyToBigDecimal);
            BigDecimal balance = balanceInNativeCurrency.add(balanceInForeignCurrency);

            showBalanceConsumer.accept(balance);
        });
    }

    private static BigDecimal extractBalance(Tuple tuple,
                                             String key,
                                             Function<Long, BigDecimal> balanceFunction) {
        Number preliminaryBalance = tuple.get(key);
        if (preliminaryBalance == null) {
            return BigDecimal.ZERO;
        }
        return balanceFunction.apply(preliminaryBalance.longValue());
    }
}
