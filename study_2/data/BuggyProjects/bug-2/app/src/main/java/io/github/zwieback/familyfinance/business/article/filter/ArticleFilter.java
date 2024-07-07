package io.github.zwieback.familyfinance.business.article.filter;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.annimon.stream.Objects;

import io.github.zwieback.familyfinance.core.filter.EntityFolderFilter;

public abstract class ArticleFilter extends EntityFolderFilter {

    private String searchName;

    ArticleFilter() {
        super();
    }

    ArticleFilter(ArticleFilter filter) {
        super(filter);
        setSearchName(filter.getSearchName());
    }

    ArticleFilter(Parcel in) {
        super(in);
    }

    @Override
    protected void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        in.writeString(searchName);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        searchName = out.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleFilter)) return false;
        if (!super.equals(o)) return false;
        ArticleFilter filter = (ArticleFilter) o;
        return Objects.equals(getSearchName(), filter.getSearchName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSearchName());
    }

    @Nullable
    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(@Nullable String searchName) {
        this.searchName = searchName;
    }
}
