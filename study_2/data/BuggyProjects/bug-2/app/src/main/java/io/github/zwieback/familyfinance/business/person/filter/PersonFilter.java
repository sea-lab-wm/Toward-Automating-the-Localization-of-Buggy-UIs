package io.github.zwieback.familyfinance.business.person.filter;

import android.os.Parcel;

import io.github.zwieback.familyfinance.core.filter.EntityFolderFilter;

public class PersonFilter extends EntityFolderFilter {

    public static final String PERSON_FILTER = "personFilter";

    public static Creator<PersonFilter> CREATOR = new Creator<PersonFilter>() {

        public PersonFilter createFromParcel(Parcel parcel) {
            return new PersonFilter(parcel);
        }

        public PersonFilter[] newArray(int size) {
            return new PersonFilter[size];
        }
    };

    public PersonFilter() {
        super();
    }

    public PersonFilter(PersonFilter filter) {
        super(filter);
    }

    private PersonFilter(Parcel in) {
        super(in);
    }
}
