package io.github.zwieback.familyfinance.business.article.filter;

import android.os.Parcel;

public class AllArticleFilter extends ArticleFilter {

    public static final String ALL_ARTICLE_FILTER = "allArticleFilter";

    public static Creator<AllArticleFilter> CREATOR =
            new Creator<AllArticleFilter>() {

                public AllArticleFilter createFromParcel(Parcel parcel) {
                    return new AllArticleFilter(parcel);
                }

                public AllArticleFilter[] newArray(int size) {
                    return new AllArticleFilter[size];
                }
            };

    public AllArticleFilter() {
        super();
    }

    public AllArticleFilter(AllArticleFilter filter) {
        super(filter);
    }

    private AllArticleFilter(Parcel in) {
        super(in);
    }
}
