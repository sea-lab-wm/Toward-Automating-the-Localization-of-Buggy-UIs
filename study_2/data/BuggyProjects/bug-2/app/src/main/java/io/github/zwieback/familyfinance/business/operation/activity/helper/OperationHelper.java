package io.github.zwieback.familyfinance.business.operation.activity.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer.OperationForceDestroyer;
import io.github.zwieback.familyfinance.business.sms.service.AddOperationImmediatelyService;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class OperationHelper<FILTER extends OperationFilter> {

    public static final String INPUT_OPERATION_TYPE = "operationType";

    final Context context;
    final ReactiveEntityStore<Persistable> data;
    final DatabasePrefs databasePrefs;

    OperationHelper(Context context, ReactiveEntityStore<Persistable> data) {
        this.context = context;
        this.data = data;
        this.databasePrefs = DatabasePrefs.with(context);
    }

    public abstract Intent getIntentToAdd();

    public abstract Intent getIntentToAdd(@Nullable Integer articleId,
                                          @Nullable Integer accountId,
                                          @Nullable Integer transferAccountId,
                                          @Nullable Integer ownerId,
                                          @Nullable Integer currencyId,
                                          @Nullable Integer exchangeRateId,
                                          @Nullable LocalDate date,
                                          @Nullable BigDecimal value,
                                          @Nullable String description,
                                          @Nullable String url);

    @NonNull
    abstract Intent getIntentToAdd(@NonNull Intent preparedIntent,
                                   @Nullable Integer articleId,
                                   @Nullable Integer accountId,
                                   @Nullable Integer transferAccountId,
                                   @Nullable Integer ownerId,
                                   @Nullable Integer currencyId,
                                   @Nullable Integer exchangeRateId,
                                   @Nullable LocalDate date,
                                   @Nullable BigDecimal value,
                                   @Nullable String description,
                                   @Nullable String url);

    public abstract Intent getIntentToAdd(@Nullable FILTER filter);

    public abstract Intent getIntentToEdit(OperationView operation);

    public abstract Intent getIntentToDuplicate(OperationView operation);

    abstract Intent getEmptyIntent();

    public abstract boolean validToAddImmediately(@Nullable Integer articleId,
                                                  @Nullable Integer accountId,
                                                  @Nullable Integer transferAccountId,
                                                  @Nullable Integer ownerId,
                                                  @Nullable Integer currencyId,
                                                  @Nullable Integer exchangeRateId,
                                                  @Nullable LocalDate date,
                                                  @Nullable BigDecimal value,
                                                  @Nullable String description,
                                                  @Nullable String url);

    @NonNull
    public abstract Intent getIntentToAddImmediately(@Nullable Integer articleId,
                                                     @Nullable Integer accountId,
                                                     @Nullable Integer transferAccountId,
                                                     @Nullable Integer ownerId,
                                                     @Nullable Integer currencyId,
                                                     @Nullable Integer exchangeRateId,
                                                     @Nullable LocalDate date,
                                                     @Nullable BigDecimal value,
                                                     @Nullable String description,
                                                     @Nullable String url);

    @NonNull
    Intent getEmptyIntentToAddImmediately() {
        return new Intent(context, AddOperationImmediatelyService.class)
                .setAction("ACTION_ADD_OPERATION_IMMEDIATELY");
    }

    @NonNull
    public abstract Disposable addOperationImmediately(@NonNull Intent intent, @NonNull Consumer<Operation> onSuccess);

    public EntityDestroyer<Operation> createDestroyer(OperationView operation) {
        return new OperationForceDestroyer(context, data);
    }
}
