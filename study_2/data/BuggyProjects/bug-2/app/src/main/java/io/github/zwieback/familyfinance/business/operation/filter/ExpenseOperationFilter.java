package io.github.zwieback.familyfinance.business.operation.filter;

import android.os.Parcel;

public class ExpenseOperationFilter extends OperationFilter {

    public static final String EXPENSE_OPERATION_FILTER = "expenseOperationFilter";

    public static Creator<ExpenseOperationFilter> CREATOR = new Creator<ExpenseOperationFilter>() {

        public ExpenseOperationFilter createFromParcel(Parcel parcel) {
            return new ExpenseOperationFilter(parcel);
        }

        public ExpenseOperationFilter[] newArray(int size) {
            return new ExpenseOperationFilter[size];
        }
    };

    public ExpenseOperationFilter() {
        super();
    }

    public ExpenseOperationFilter(ExpenseOperationFilter filter) {
        super(filter);
    }

    private ExpenseOperationFilter(Parcel in) {
        super(in);
    }
}
