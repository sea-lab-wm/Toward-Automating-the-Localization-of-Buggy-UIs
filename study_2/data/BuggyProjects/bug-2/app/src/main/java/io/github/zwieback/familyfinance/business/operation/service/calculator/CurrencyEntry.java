package io.github.zwieback.familyfinance.business.operation.service.calculator;

import com.annimon.stream.Objects;

import io.github.zwieback.familyfinance.core.model.OperationView;

final class CurrencyEntry {

    final String name;
    private final int id;

    CurrencyEntry(OperationView operation) {
        this.id = operation.getCurrencyId();
        this.name = operation.getCurrencyName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyEntry)) return false;
        CurrencyEntry that = (CurrencyEntry) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
