package io.github.zwieback.familyfinance.core.model.converter;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDate;

import java.sql.Date;

import io.requery.Converter;

public class LocalDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Class<LocalDate> getMappedType() {
        return LocalDate.class;
    }

    @Override
    public Class<Date> getPersistedType() {
        return Date.class;
    }

    @Override
    public Integer getPersistedSize() {
        return null;
    }

    @Override
    public Date convertToPersisted(LocalDate value) {
        return value == null ? null : DateTimeUtils.toSqlDate(value);
    }

    @Override
    public LocalDate convertToMapped(Class<? extends LocalDate> type, Date value) {
        return value == null ? null : DateTimeUtils.toLocalDate(value);
    }
}
