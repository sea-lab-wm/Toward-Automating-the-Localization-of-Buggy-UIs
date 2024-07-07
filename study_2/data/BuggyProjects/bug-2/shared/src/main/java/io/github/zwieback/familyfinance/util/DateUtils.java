package io.github.zwieback.familyfinance.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.chrono.IsoChronology;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.format.DateTimeParseException;
import org.threeten.bp.format.ResolverStyle;
import org.threeten.bp.format.SignStyle;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.IsoFields;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.util.Calendar;

import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.isNullId;
import static io.github.zwieback.familyfinance.util.StringUtils.EMPTY;
import static io.github.zwieback.familyfinance.util.StringUtils.isTextEmpty;

public final class DateUtils {

    public static final DateTimeFormatter ISO_LOCAL_WEEK;
    public static final DateTimeFormatter ISO_LOCAL_MONTH;
    public static final DateTimeFormatter ISO_LOCAL_QUARTER;
    public static final DateTimeFormatter SBERBANK_DATE_FORMATTER;

    private static final LocalDate EPOCH_DATE;

    // bundle keys
    private static final String KEY_YEAR = "keyYear";
    private static final String KEY_MONTH = "keyMonth";
    private static final String KEY_DAY_OF_MONTH = "keyDayOfMonth";

    // workaround for month of calendar (start with 0)
    private static final int MONTH_OF_CALENDAR_INCREMENT = 1;

    static {
        ISO_LOCAL_WEEK = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-')
                .appendValue(ChronoField.ALIGNED_WEEK_OF_YEAR, 2)
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT)
                .withChronology(IsoChronology.INSTANCE);

        ISO_LOCAL_MONTH = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-')
                .appendValue(ChronoField.MONTH_OF_YEAR, 2)
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT)
                .withChronology(IsoChronology.INSTANCE);

        ISO_LOCAL_QUARTER = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral("-Q")
                .appendValue(IsoFields.QUARTER_OF_YEAR, 1)
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT)
                .withChronology(IsoChronology.INSTANCE);

        SBERBANK_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");

        EPOCH_DATE = getEpochDay();
    }

    public static boolean isTextAnLocalDate(@Nullable String text) {
        try {
            return stringToLocalDate(text) != null;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Nullable
    public static LocalDate stringToLocalDate(@Nullable String text) {
        if (isTextEmpty(text)) {
            return null;
        }
        return LocalDate.parse(text);
    }

    @NonNull
    public static LocalDate sberbankDateToLocalDate(@Nullable String text) {
        if (isTextEmpty(text)) {
            return now();
        }
        return LocalDate.from(SBERBANK_DATE_FORMATTER.parse(text));
    }

    @NonNull
    public static String localDateToString(@Nullable LocalDate localDate) {
        if (localDate == null) {
            return EMPTY;
        }
        return localDate.toString();
    }

    @NonNull
    public static String localDateToString(@Nullable LocalDate localDate,
                                           @NonNull DateTimeFormatter formatter) {
        if (localDate == null) {
            return EMPTY;
        }
        return localDate.format(formatter);
    }

    @NonNull
    public static Calendar localDateToCalendar(@Nullable LocalDate localDate) {
        if (localDate == null) {
            return Calendar.getInstance();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDate.getYear(), localDate.getMonthValue() - MONTH_OF_CALENDAR_INCREMENT,
                localDate.getDayOfMonth());
        return calendar;
    }

    @NonNull
    public static LocalDate calendarDateToLocalDate(int calendarYear,
                                                    int calendarMonth,
                                                    int calendarDay) {
        return LocalDate.of(calendarYear, calendarMonth + MONTH_OF_CALENDAR_INCREMENT, calendarDay);
    }

    @NonNull
    public static LocalDate now() {
        return LocalDate.now();
    }

    @NonNull
    public static LocalDate startOfMonth() {
        return now().with(TemporalAdjusters.firstDayOfMonth());
    }

    @NonNull
    public static LocalDate endOfMonth() {
        return now().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static void writeLocalDateToBundle(Bundle out, @NonNull LocalDate date) {
        out.putInt(KEY_YEAR, date.getYear());
        out.putInt(KEY_MONTH, date.getMonthValue());
        out.putInt(KEY_DAY_OF_MONTH, date.getDayOfMonth());
    }

    @NonNull
    public static LocalDate readLocalDateFromBundle(Bundle in) {
        int year = in.getInt(KEY_YEAR);
        int month = in.getInt(KEY_MONTH);
        int dayOfMonth = in.getInt(KEY_DAY_OF_MONTH);
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static void writeLocalDateToIntent(@NonNull Intent out,
                                              @NonNull String name,
                                              @NonNull LocalDate date) {
        out.putExtra(name + KEY_YEAR, date.getYear());
        out.putExtra(name + KEY_MONTH, date.getMonthValue());
        out.putExtra(name + KEY_DAY_OF_MONTH, date.getDayOfMonth());
    }

    @NonNull
    public static LocalDate readLocalDateFromIntent(@NonNull Intent in, @NonNull String name) {
        LocalDate today = now();
        int year = in.getIntExtra(name + KEY_YEAR, today.getYear());
        int month = in.getIntExtra(name + KEY_MONTH, today.getMonthValue());
        int dayOfMonth = in.getIntExtra(name + KEY_DAY_OF_MONTH, today.getDayOfMonth());
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static void writeLocalDateToParcel(Parcel out, @Nullable LocalDate date) {
        if (date == null) {
            out.writeInt(ID_AS_NULL);
            out.writeInt(ID_AS_NULL);
            out.writeInt(ID_AS_NULL);
        } else {
            out.writeInt(date.getYear());
            out.writeInt(date.getMonthValue());
            out.writeInt(date.getDayOfMonth());
        }
    }

    @Nullable
    public static LocalDate readLocalDateFromParcel(Parcel in) {
        int year = in.readInt();
        int month = in.readInt();
        int dayOfMonth = in.readInt();
        if (isNullId(year) || isNullId(month) || isNullId(dayOfMonth)) {
            return null;
        }
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static long localDateToEpochDay(@NonNull LocalDate date) {
        return ChronoUnit.DAYS.between(EPOCH_DATE, date);
    }

    @NonNull
    public static LocalDate epochDayToLocalDate(long daysFromEpoch) {
        return EPOCH_DATE.plusDays(daysFromEpoch);
    }

    public static long localDateToEpochWeek(@NonNull LocalDate date) {
        return ChronoUnit.WEEKS.between(EPOCH_DATE, date);
    }

    @NonNull
    public static LocalDate epochWeekToLocalDate(long monthsFromWeek) {
        return EPOCH_DATE.plusWeeks(monthsFromWeek);
    }

    public static long localDateToEpochMonth(@NonNull LocalDate date) {
        return ChronoUnit.MONTHS.between(EPOCH_DATE, date);
    }

    @NonNull
    public static LocalDate epochMonthToLocalDate(long monthsFromEpoch) {
        return EPOCH_DATE.plusMonths(monthsFromEpoch);
    }

    public static long localDateToEpochQuarter(@NonNull LocalDate date) {
        return IsoFields.QUARTER_YEARS.between(EPOCH_DATE, date);
    }

    @NonNull
    public static LocalDate epochQuarterToLocalDate(long quartersFromWeek) {
        return plusQuarters(EPOCH_DATE, quartersFromWeek);
    }

    @NonNull
    public static LocalDate plusQuarters(@NonNull LocalDate date, long numberOfQuarters) {
        return date.plus(numberOfQuarters, IsoFields.QUARTER_YEARS);
    }

    public static int extractWeekOfYear(@NonNull LocalDate date) {
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    public static int extractQuarterOfYear(@NonNull LocalDate date) {
        return date.get(IsoFields.QUARTER_OF_YEAR);
    }

    private DateUtils() {
    }

    private static LocalDate getEpochDay() {
        return LocalDate.of(1970, Month.JANUARY, 1);
    }
}
