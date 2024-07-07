package io.github.zwieback.familyfinance.business.operation.filter;

import android.os.Parcel;

public class TransferOperationFilter extends OperationFilter {

    public static final String TRANSFER_OPERATION_FILTER = "transferOperationFilter";

    public static Creator<TransferOperationFilter> CREATOR =
            new Creator<TransferOperationFilter>() {

                public TransferOperationFilter createFromParcel(Parcel parcel) {
                    return new TransferOperationFilter(parcel);
                }

                public TransferOperationFilter[] newArray(int size) {
                    return new TransferOperationFilter[size];
                }
            };

    public TransferOperationFilter() {
        super();
    }

    public TransferOperationFilter(TransferOperationFilter filter) {
        super(filter);
    }

    private TransferOperationFilter(Parcel in) {
        super(in);
    }
}
