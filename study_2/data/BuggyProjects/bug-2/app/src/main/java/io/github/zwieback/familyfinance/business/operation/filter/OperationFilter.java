package io.github.zwieback.familyfinance.business.operation.filter;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Objects;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import io.github.zwieback.familyfinance.util.DateUtils;

import static io.github.zwieback.familyfinance.util.DateUtils.readLocalDateFromParcel;
import static io.github.zwieback.familyfinance.util.DateUtils.writeLocalDateToParcel;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.intToIntegerId;
import static io.github.zwieback.familyfinance.util.NumberUtils.integerToIntId;
import static io.github.zwieback.familyfinance.util.NumberUtils.readBigDecimalFromParcel;
import static io.github.zwieback.familyfinance.util.NumberUtils.writeBigDecimalToParcel;

public abstract class OperationFilter extends EntityFilter {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal startValue;
    private BigDecimal endValue;
    private int articleId;
    private int accountId;
    private int ownerId;
    private int currencyId;

    OperationFilter() {
        super();
    }

    OperationFilter(OperationFilter filter) {
        super(filter);
        setStartDate(filter.getStartDate());
        setEndDate(filter.getEndDate());
        setStartValue(filter.getStartValue());
        setEndValue(filter.getEndValue());
        setArticleId(filter.getArticleId());
        setAccountId(filter.getAccountId());
        setOwnerId(filter.getOwnerId());
        setCurrencyId(filter.getCurrencyId());
    }

    OperationFilter(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        startDate = DateUtils.startOfMonth();
        endDate = DateUtils.endOfMonth();
        articleId = ID_AS_NULL;
        accountId = ID_AS_NULL;
        ownerId = ID_AS_NULL;
        currencyId = ID_AS_NULL;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        startDate = readLocalDateFromParcel(in);
        endDate = readLocalDateFromParcel(in);
        startValue = readBigDecimalFromParcel(in);
        endValue = readBigDecimalFromParcel(in);
        articleId = in.readInt();
        accountId = in.readInt();
        ownerId = in.readInt();
        currencyId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        writeLocalDateToParcel(out, startDate);
        writeLocalDateToParcel(out, endDate);
        writeBigDecimalToParcel(out, startValue);
        writeBigDecimalToParcel(out, endValue);
        out.writeInt(articleId);
        out.writeInt(accountId);
        out.writeInt(ownerId);
        out.writeInt(currencyId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationFilter)) return false;
        OperationFilter that = (OperationFilter) o;
        return Objects.equals(getStartDate(), that.getStartDate()) &&
                Objects.equals(getEndDate(), that.getEndDate()) &&
                Objects.equals(getStartValue(), that.getStartValue()) &&
                Objects.equals(getEndValue(), that.getEndValue()) &&
                Objects.equals(getArticleId(), that.getArticleId()) &&
                Objects.equals(getAccountId(), that.getAccountId()) &&
                Objects.equals(getOwnerId(), that.getOwnerId()) &&
                Objects.equals(getCurrencyId(), that.getCurrencyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartDate(), getEndDate(), getStartValue(), getEndValue(),
                getArticleId(), getAccountId(), getOwnerId(), getCurrencyId());
    }

    @NonNull
    public final LocalDate getStartDate() {
        return startDate;
    }

    public final void setStartDate(@NonNull LocalDate startDate) {
        this.startDate = startDate;
    }

    @NonNull
    public final LocalDate getEndDate() {
        return endDate;
    }

    public final void setEndDate(@NonNull LocalDate endDate) {
        this.endDate = endDate;
    }

    @Nullable
    public final BigDecimal getStartValue() {
        return startValue;
    }

    public final void setStartValue(@Nullable BigDecimal startValue) {
        this.startValue = startValue;
    }

    @Nullable
    public final BigDecimal getEndValue() {
        return endValue;
    }

    public final void setEndValue(@Nullable BigDecimal endValue) {
        this.endValue = endValue;
    }

    @Nullable
    public final Integer getArticleId() {
        return intToIntegerId(articleId);
    }

    public final void setArticleId(@Nullable Integer articleId) {
        this.articleId = integerToIntId(articleId);
    }

    @Nullable
    public final Integer getAccountId() {
        return intToIntegerId(accountId);
    }

    public final void setAccountId(@Nullable Integer accountId) {
        this.accountId = integerToIntId(accountId);
    }

    @Nullable
    public final Integer getOwnerId() {
        return intToIntegerId(ownerId);
    }

    public final void setOwnerId(@Nullable Integer ownerId) {
        this.ownerId = integerToIntId(ownerId);
    }

    @Nullable
    public final Integer getCurrencyId() {
        return intToIntegerId(currencyId);
    }

    public final void setCurrencyId(@Nullable Integer currencyId) {
        this.currencyId = integerToIntId(currencyId);
    }
}
