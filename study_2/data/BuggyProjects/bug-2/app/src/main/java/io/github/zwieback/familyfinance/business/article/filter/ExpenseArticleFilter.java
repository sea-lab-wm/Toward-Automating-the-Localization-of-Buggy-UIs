package io.github.zwieback.familyfinance.business.article.filter;

import android.os.Parcel;

public class ExpenseArticleFilter extends ArticleFilter {

    public static final String EXPENSE_ARTICLE_FILTER = "expenseArticleFilter";

    public static Creator<ExpenseArticleFilter> CREATOR = new Creator<ExpenseArticleFilter>() {

        public ExpenseArticleFilter createFromParcel(Parcel parcel) {
            return new ExpenseArticleFilter(parcel);
        }

        public ExpenseArticleFilter[] newArray(int size) {
            return new ExpenseArticleFilter[size];
        }
    };

    public ExpenseArticleFilter() {
        super();
    }

    public ExpenseArticleFilter(ExpenseArticleFilter filter) {
        super(filter);
    }

    private ExpenseArticleFilter(Parcel in) {
        super(in);
    }
}
