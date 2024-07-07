package io.github.zwieback.familyfinance.core.model;

import android.databinding.Bindable;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.core.model.converter.BigDecimalToWorthConverter;
import io.github.zwieback.familyfinance.core.model.restriction.AccountRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.CurrencyRestriction;
import io.github.zwieback.familyfinance.core.model.restriction.PersonRestriction;
import io.github.zwieback.familyfinance.core.model.type.AccountType;
import io.requery.Column;
import io.requery.Convert;
import io.requery.Entity;
import io.requery.Nullable;
import io.requery.PropertyNameStyle;
import io.requery.View;

@Entity(propertyNameStyle = PropertyNameStyle.FLUENT_BEAN)
@View(name = "v_account")
public interface IAccountView extends IBaseEntityFolder {

    @Column(nullable = false, value = "true")
    boolean isActive();

    @Nullable
    @Column(name = "parent_id")
    Integer getParentId();

    @Bindable
    @Nullable
    @Column(name = "parent_name", length = AccountRestriction.NAME_MAX_LENGTH)
    String getParentName();

    /**
     * May be {@code null} only if {@link #isFolder()}
     */
    @Nullable
    @Column(name = "currency_id")
    Integer getCurrencyId();

    /**
     * May be {@code null} only if {@link #isFolder()}
     */
    @Bindable
    @Nullable
    @Column(name = "currency_name", length = CurrencyRestriction.NAME_MAX_LENGTH)
    String getCurrencyName();

    /**
     * May be {@code null} only if {@link #isFolder()}
     */
    @Nullable
    @Column(name = "owner_id")
    Integer getOwnerId();

    /**
     * May be {@code null} only if {@link #isFolder()}
     */
    @Bindable
    @Nullable
    @Column(name = "owner_name", length = PersonRestriction.NAME_MAX_LENGTH)
    String getOwnerName();

    @Bindable
    @Column(nullable = false, length = AccountRestriction.NAME_MAX_LENGTH)
    String getName();

    /**
     * May be {@code null} only if {@link #isFolder()}
     */
    @Bindable
    @Nullable
    @Column(name = "initial_balance")
    @Convert(BigDecimalToWorthConverter.class)
    BigDecimal getInitialBalance();

    @Bindable
    @Column(name = "order_code", nullable = false)
    int getOrderCode();

    @Column(name = "_type", nullable = false, length = AccountRestriction.TYPE_MAX_LENGTH, value = "UNDEFINED_ACCOUNT")
    AccountType getType();

    @Bindable
    @Column(name = "_number", length = AccountRestriction.NUMBER_MAX_LENGTH)
    String getNumber();

    @Bindable
    @Column(name = "payment_system", length = AccountRestriction.PAYMENT_SYSTEM_MAX_LENGTH)
    String getPaymentSystem();

    /**
     * May be {@code null} only if {@link AccountType#isCard()} is {@code false}
     */
    @Bindable
    @Column(name = "card_number", length = AccountRestriction.CARD_NUMBER_MAX_LENGTH)
    String getCardNumber();
}
