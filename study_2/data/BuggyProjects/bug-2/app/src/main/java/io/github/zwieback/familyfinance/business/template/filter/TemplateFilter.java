package io.github.zwieback.familyfinance.business.template.filter;

import android.os.Parcel;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;

public class TemplateFilter extends EntityFilter {

    public static final String TEMPLATE_FILTER = "templateFilter";

    public static Creator<TemplateFilter> CREATOR = new Creator<TemplateFilter>() {

        public TemplateFilter createFromParcel(Parcel parcel) {
            return new TemplateFilter(parcel);
        }

        public TemplateFilter[] newArray(int size) {
            return new TemplateFilter[size];
        }
    };

    public TemplateFilter() {
        super();
    }

    public TemplateFilter(TemplateFilter filter) {
        super(filter);
    }

    private TemplateFilter(Parcel in) {
        super(in);
    }
}
