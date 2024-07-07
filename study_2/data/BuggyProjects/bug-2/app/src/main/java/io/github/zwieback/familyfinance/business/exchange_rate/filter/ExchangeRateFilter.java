package io.github.zwieback.familyfinance.business.exchange_rate.filter;

import android.os.Parcel;
import android.support.annotation.Nullable;

import com.annimon.stream.Objects;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.filter.EntityFilter;

import static io.github.zwieback.familyfinance.util.DateUtils.readLocalDateFromParcel;
import static io.github.zwieback.familyfinance.util.DateUtils.writeLocalDateToParcel;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.intToIntegerId;
import static io.github.zwieback.familyfinance.util.NumberUtils.integerToIntId;
import static io.github.zwieback.familyfinance.util.NumberUtils.readBigDecimalFromParcel;
import static io.github.zwieback.familyfinance.util.NumberUtils.writeBigDecimalToParcel;

public class ExchangeRateFilter extends EntityFilter {

    public static final String EXCHANGE_RATE_FILTER = "exchangeRateFilter";

    public static Creator<ExchangeRateFilter> CREATOR = new Creator<ExchangeRateFilter>() {

        public ExchangeRateFilter createFromParcel(Parcel parcel) {
            return new ExchangeRateFilter(parcel);
        }

        public ExchangeRateFilter[] newArray(int size) {
            return new ExchangeRateFilter[size];
        }
    };

    private int currencyId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal startValue;
    private BigDecimal endValue;

    public ExchangeRateFilter() {
        super();
    }

    public ExchangeRateFilter(ExchangeRateFilter filter) {
        super(filter);
        setCurrencyId(filter.getCurrencyId());
        setStartDate(filter.getStartDate());
        setEndDate(filter.getEndDate());
        setStartValue(filter.getStartValue());
        setEndValue(filter.getEndValue());
    }

    private ExchangeRateFilter(Parcel in) {
        super(in);
    }

    @Override
    protected void init() {
        super.init();
        currencyId = ID_AS_NULL;
    }

    @Override
    protected void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        currencyId = in.readInt();
        startDate = readLocalDateFromParcel(in);
        endDate = readLocalDateFromParcel(in);
        startValue = readBigDecimalFromParcel(in);
        endValue = readBigDecimalFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(currencyId);
        writeLocalDateToParcel(out, startDate);
        writeLocalDateToParcel(out, endDate);
        writeBigDecimalToParcel(out, startValue);
        writeBigDecimalToParcel(out, endValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeRateFilter)) return false;
        if (!super.equals(o)) return false;
        ExchangeRateFilter that = (ExchangeRateFilter) o;
        return Objects.equals(getCurrencyId(), that.getCurrencyId()) &&
                Objects.equals(getStartDate(), that.getStartDate()) &&
                Objects.equals(getEndDate(), that.getEndDate()) &&
                Objects.equals(getStartValue(), that.getStartValue()) &&
                Objects.equals(getEndValue(), that.getEndValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCurrencyId(), getStartDate(), getEndDate(),
                getStartValue(), getEndValue());
    }

    @Nullable
    public final Integer getCurrencyId() {
        return intToIntegerId(currencyId);
    }

    public final void setCurrencyId(@Nullable Integer currencyId) {
        this.currencyId = integerToIntId(currencyId);
    }

    @Nullable
    public final LocalDate getStartDate() {
        return startDate;
    }

    public final void setStartDate(@Nullable LocalDate startDate) {
        this.startDate = startDate;
    }

    @Nullable
    public final LocalDate getEndDate() {
        return endDate;
    }

    public final void setEndDate(@Nullable LocalDate endDate) {
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
}
