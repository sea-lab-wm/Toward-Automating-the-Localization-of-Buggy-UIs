package io.github.zwieback.familyfinance.business.account.filter;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.annimon.stream.Objects;

import io.github.zwieback.familyfinance.core.filter.EntityFolderFilter;

import static io.github.zwieback.familyfinance.util.BooleanUtils.readBooleanFromParcel;
import static io.github.zwieback.familyfinance.util.BooleanUtils.writeBooleanToParcel;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.intToIntegerId;
import static io.github.zwieback.familyfinance.util.NumberUtils.integerToIntId;

public class AccountFilter extends EntityFolderFilter {

    public static final String ACCOUNT_FILTER = "accountFilter";

    public static Creator<AccountFilter> CREATOR = new Creator<AccountFilter>() {

        public AccountFilter createFromParcel(Parcel parcel) {
            return new AccountFilter(parcel);
        }

        public AccountFilter[] newArray(int size) {
            return new AccountFilter[size];
        }
    };

    private int ownerId;
    private boolean onlyActive;

    public AccountFilter() {
        super();
    }

    public AccountFilter(AccountFilter filter) {
        super(filter);
        setOwnerId(filter.getOwnerId());
        setOnlyActive(filter.isOnlyActive());
    }

    private AccountFilter(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        super.init();
        ownerId = ID_AS_NULL;
        onlyActive = false;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        ownerId = in.readInt();
        onlyActive = readBooleanFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(ownerId);
        writeBooleanToParcel(out, onlyActive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountFilter)) return false;
        if (!super.equals(o)) return false;
        AccountFilter that = (AccountFilter) o;
        return Objects.equals(getOwnerId(), that.getOwnerId()) &&
                Objects.equals(isOnlyActive(), that.isOnlyActive());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getOwnerId(), isOnlyActive());
    }

    @Nullable
    public final Integer getOwnerId() {
        return intToIntegerId(ownerId);
    }

    public final void setOwnerId(@Nullable Integer ownerId) {
        this.ownerId = integerToIntId(ownerId);
    }

    public boolean isOnlyActive() {
        return onlyActive;
    }

    public void setOnlyActive(boolean onlyActive) {
        this.onlyActive = onlyActive;
    }
}
