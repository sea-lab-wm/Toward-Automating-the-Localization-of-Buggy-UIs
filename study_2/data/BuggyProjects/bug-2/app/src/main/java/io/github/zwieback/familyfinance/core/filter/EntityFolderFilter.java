package io.github.zwieback.familyfinance.core.filter;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.annimon.stream.Objects;

import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.intToIntegerId;
import static io.github.zwieback.familyfinance.util.NumberUtils.integerToIntId;

public abstract class EntityFolderFilter extends EntityFilter {

    private int parentId;

    protected EntityFolderFilter() {
        super();
    }

    protected EntityFolderFilter(EntityFolderFilter filter) {
        super(filter);
        setParentId(filter.getParentId());
    }

    protected EntityFolderFilter(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        parentId = ID_AS_NULL;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        in.writeInt(parentId);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        parentId = out.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityFolderFilter)) return false;
        EntityFolderFilter that = (EntityFolderFilter) o;
        return Objects.equals(getParentId(), that.getParentId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getParentId());
    }

    @Nullable
    public final Integer getParentId() {
        return intToIntegerId(parentId);
    }

    public final void setParentId(@Nullable Integer parentId) {
        this.parentId = integerToIntId(parentId);
    }
}
