package io.github.zwieback.familyfinance.business.operation.filter;

import android.os.Parcel;

public class FlowOfFundsOperationFilter extends OperationFilter {

    public static final String FLOW_OF_FUNDS_OPERATION_FILTER = "flowOfFundsOperationFilter";

    public static Creator<FlowOfFundsOperationFilter> CREATOR =
            new Creator<FlowOfFundsOperationFilter>() {

                public FlowOfFundsOperationFilter createFromParcel(Parcel parcel) {
                    return new FlowOfFundsOperationFilter(parcel);
                }

                public FlowOfFundsOperationFilter[] newArray(int size) {
                    return new FlowOfFundsOperationFilter[size];
                }
            };

    public FlowOfFundsOperationFilter() {
        super();
    }

    public FlowOfFundsOperationFilter(FlowOfFundsOperationFilter filter) {
        super(filter);
    }

    private FlowOfFundsOperationFilter(Parcel in) {
        super(in);
    }
}
