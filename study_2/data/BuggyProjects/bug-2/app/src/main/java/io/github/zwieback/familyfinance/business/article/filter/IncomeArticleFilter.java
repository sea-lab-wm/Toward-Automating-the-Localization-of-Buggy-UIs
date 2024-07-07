package io.github.zwieback.familyfinance.business.article.filter;

import android.os.Parcel;

public class IncomeArticleFilter extends ArticleFilter {

    public static final String INCOME_ARTICLE_FILTER = "incomeArticleFilter";

    public static Creator<IncomeArticleFilter> CREATOR = new Creator<IncomeArticleFilter>() {

        public IncomeArticleFilter createFromParcel(Parcel parcel) {
            return new IncomeArticleFilter(parcel);
        }

        public IncomeArticleFilter[] newArray(int size) {
            return new IncomeArticleFilter[size];
        }
    };

    public IncomeArticleFilter() {
        super();
    }

    public IncomeArticleFilter(IncomeArticleFilter filter) {
        super(filter);
    }

    private IncomeArticleFilter(Parcel in) {
        super(in);
    }
}
