package io.github.zwieback.familyfinance.business.operation.filter;

import android.os.Parcel;

public class IncomeOperationFilter extends OperationFilter {

    public static final String INCOME_OPERATION_FILTER = "incomeOperationFilter";

    public static Creator<IncomeOperationFilter> CREATOR = new Creator<IncomeOperationFilter>() {

        public IncomeOperationFilter createFromParcel(Parcel parcel) {
            return new IncomeOperationFilter(parcel);
        }

        public IncomeOperationFilter[] newArray(int size) {
            return new IncomeOperationFilter[size];
        }
    };

    public IncomeOperationFilter() {
        super();
    }

    public IncomeOperationFilter(IncomeOperationFilter filter) {
        super(filter);
    }

    private IncomeOperationFilter(Parcel in) {
        super(in);
    }
}
