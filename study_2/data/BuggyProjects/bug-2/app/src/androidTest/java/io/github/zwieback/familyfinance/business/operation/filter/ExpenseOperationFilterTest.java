package io.github.zwieback.familyfinance.business.operation.filter;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.zwieback.familyfinance.util.RandomGenerator;

@RunWith(AndroidJUnit4.class)
public class ExpenseOperationFilterTest {

    private RandomGenerator randomGenerator;

    @Before
    public void setUp() {
        randomGenerator = new RandomGenerator();
    }

    @Test
    public void copyOfFilter_isEqual() {
        ExpenseOperationFilter filter = new ExpenseOperationFilter();
        filter.setArticleId(randomGenerator.getRandomInt());
        filter.setAccountId(randomGenerator.getRandomInt());
        filter.setOwnerId(randomGenerator.getRandomInt());
        filter.setCurrencyId(randomGenerator.getRandomInt());
        filter.setStartDate(randomGenerator.getRandomLocalDate());
        filter.setEndDate(randomGenerator.getRandomLocalDate());
        filter.setStartValue(randomGenerator.getRandomBigDecimal());
        filter.setEndValue(randomGenerator.getRandomBigDecimal());

        ExpenseOperationFilter filterCopy = new ExpenseOperationFilter(filter);
        Assert.assertEquals(filter, filterCopy);
    }
}
