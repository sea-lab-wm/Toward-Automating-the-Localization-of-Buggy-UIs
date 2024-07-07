package io.github.zwieback.familyfinance.business.operation.activity.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.business.operation.activity.TransferOperationEditActivity;
import io.github.zwieback.familyfinance.business.operation.filter.TransferOperationFilter;
import io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer.TransferOperationForceDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.github.zwieback.familyfinance.util.StringUtils;
import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class TransferOperationHelper extends OperationHelper<TransferOperationFilter> {

    public TransferOperationHelper(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    public Intent getIntentToAdd() {
        return getEmptyIntent();
    }

    @Override
    public Intent getIntentToAdd(@Nullable Integer ignoredArticleId,
                                 @Nullable Integer accountId,
                                 @Nullable Integer transferAccountId,
                                 @Nullable Integer ownerId,
                                 @Nullable Integer currencyId,
                                 @Nullable Integer exchangeRateId,
                                 @Nullable LocalDate date,
                                 @Nullable BigDecimal value,
                                 @Nullable String description,
                                 @Nullable String url) {
        Intent intent = getEmptyIntent();
        return getIntentToAdd(
                intent,
                ignoredArticleId,
                accountId, transferAccountId,
                ownerId,
                currencyId, exchangeRateId,
                date, value,
                description, url
        );
    }

    @NonNull
    @Override
    Intent getIntentToAdd(@NonNull Intent preparedIntent,
                          @Nullable Integer ignoredArticleId,
                          @Nullable Integer accountId,
                          @Nullable Integer transferAccountId,
                          @Nullable Integer ownerId,
                          @Nullable Integer currencyId,
                          @Nullable Integer exchangeRateId,
                          @Nullable LocalDate date,
                          @Nullable BigDecimal value,
                          @Nullable String description,
                          @Nullable String url) {
        if (accountId != null) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_EXPENSE_ACCOUNT_ID, accountId);
        }
        if (transferAccountId != null) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_INCOME_ACCOUNT_ID,
                    transferAccountId);
        }
        if (ownerId != null) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_EXPENSE_OWNER_ID, ownerId);
        }
        if (currencyId != null) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_EXPENSE_CURRENCY_ID, currencyId);
        }
        if (exchangeRateId != null) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_EXPENSE_EXCHANGE_RATE_ID,
                    exchangeRateId);
        }
        if (date != null) {
            DateUtils.writeLocalDateToIntent(preparedIntent,
                    TransferOperationEditActivity.INPUT_EXPENSE_DATE, date);
        }
        if (value != null) {
            NumberUtils.writeBigDecimalToIntent(preparedIntent,
                    TransferOperationEditActivity.INPUT_EXPENSE_VALUE, value);
        }
        if (StringUtils.isTextNotEmpty(description)) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_EXPENSE_DESCRIPTION, description);
        }
        if (StringUtils.isTextNotEmpty(url)) {
            preparedIntent.putExtra(TransferOperationEditActivity.INPUT_EXPENSE_URL, url);
        }
        return preparedIntent;
    }

    @Override
    public Intent getIntentToAdd(@Nullable TransferOperationFilter filter) {
        if (filter == null) {
            return getEmptyIntent();
        }
        return getIntentToAdd(null, filter.getAccountId(), null,
                filter.getOwnerId(), filter.getCurrencyId(), null, null, null, null, null);
    }

    @Override
    public Intent getIntentToEdit(OperationView operation) {
        Intent intent = getEmptyIntent();
        intent.putExtra(TransferOperationEditActivity.INPUT_TRANSFER_OPERATION_ID,
                TransferOperationQualifier.determineTransferExpenseOperationId(operation));
        return intent;
    }

    @Override
    public Intent getIntentToDuplicate(OperationView operation) {
        OperationView expenseOperation = TransferOperationFinder.findExpenseOperation(data,
                operation);
        OperationView incomeOperation = TransferOperationFinder.findIncomeOperation(data,
                operation);
        return getIntentToAdd(null, expenseOperation.getAccountId(), incomeOperation.getAccountId(),
                expenseOperation.getOwnerId(), expenseOperation.getCurrencyId(),
                expenseOperation.getExchangeRateId(), expenseOperation.getDate(),
                expenseOperation.getValue(), expenseOperation.getDescription(), expenseOperation.getUrl());
    }

    @Override
    Intent getEmptyIntent() {
        return new Intent(context, TransferOperationEditActivity.class);
    }

    @Override
    public boolean validToAddImmediately(@Nullable Integer ignoredArticleId,
                                         @Nullable Integer accountId,
                                         @Nullable Integer transferAccountId,
                                         @Nullable Integer ownerId,
                                         @Nullable Integer currencyId,
                                         @Nullable Integer exchangeRateId,
                                         @Nullable LocalDate date,
                                         @Nullable BigDecimal value,
                                         @Nullable String description,
                                         @Nullable String url) {
        return accountId != null
                && transferAccountId != null
                && ownerId != null
                && exchangeRateId != null
                && date != null
                && value != null;
    }

    @NonNull
    @Override
    public Intent getIntentToAddImmediately(@Nullable Integer ignoredArticleId,
                                            @Nullable Integer accountId,
                                            @Nullable Integer transferAccountId,
                                            @Nullable Integer ownerId,
                                            @Nullable Integer currencyId,
                                            @Nullable Integer exchangeRateId,
                                            @Nullable LocalDate date,
                                            @Nullable BigDecimal value,
                                            @Nullable String description,
                                            @Nullable String url) {
        Intent intent = getEmptyIntentToAddImmediately();
        return getIntentToAdd(
                intent,
                ignoredArticleId,
                accountId, transferAccountId,
                ownerId,
                currencyId, exchangeRateId,
                date, value,
                description, url
        );
    }

    @NonNull
    @Override
    Intent getEmptyIntentToAddImmediately() {
        return super.getEmptyIntentToAddImmediately()
                .putExtra(INPUT_OPERATION_TYPE, OperationType.TRANSFER_EXPENSE_OPERATION);
    }

    /**
     * What's going on here:
     * <ol>
     * <li>
     * Created transfer expense operation without linked (transfer income)
     * operation.
     * <li>
     * Created duplicate of transfer expense operation with income type and
     * transfer expense operation as linked operation, that was created on
     * the previous step.
     * <li>
     * Updated transfer expense operation with transfer income operation as
     * linked operation, that was created on the previous step.
     * </ol>
     */
    @NonNull
    @Override
    public Disposable addOperationImmediately(@NonNull Intent intent, @NonNull Consumer<Operation> onSuccess) {
        int expenseAccountId = intent.getIntExtra(TransferOperationEditActivity.INPUT_EXPENSE_ACCOUNT_ID, 0);
        int incomeAccountId = intent.getIntExtra(TransferOperationEditActivity.INPUT_INCOME_ACCOUNT_ID, 0);
        int articleId = databasePrefs.getTransferArticleId();
        int ownerId = intent.getIntExtra(TransferOperationEditActivity.INPUT_EXPENSE_OWNER_ID, 0);
        int exchangeRateId = intent.getIntExtra(TransferOperationEditActivity.INPUT_EXPENSE_EXCHANGE_RATE_ID, 0);
        LocalDate date = DateUtils.readLocalDateFromIntent(intent, TransferOperationEditActivity.INPUT_EXPENSE_DATE);
        BigDecimal value = NumberUtils.readBigDecimalFromIntent(intent, TransferOperationEditActivity.INPUT_EXPENSE_VALUE);
        String description = intent.getStringExtra(TransferOperationEditActivity.INPUT_EXPENSE_DESCRIPTION);
        String url = intent.getStringExtra(TransferOperationEditActivity.INPUT_EXPENSE_URL);
        return Maybe.zip(
                data.findByKey(Account.class, expenseAccountId),
                data.findByKey(Article.class, articleId),
                data.findByKey(Person.class, ownerId),
                data.findByKey(ExchangeRate.class, exchangeRateId),
                (expenseAccount, article, owner, exchangeRate) ->
                        new Operation()
                                .setType(OperationType.TRANSFER_EXPENSE_OPERATION)
                                .setAccount(expenseAccount)
                                .setArticle(article)
                                .setOwner(owner)
                                .setExchangeRate(exchangeRate)
                                .setDate(date)
                                .setValue(value)
                                .setDescription(description)
                                .setUrl(url)
        )
                .flatMapSingle(transferExpenseOperation -> data
                        .insert(transferExpenseOperation)
                        .flatMap(transferExpenseOperationToo -> data
                                .findByKey(Account.class, incomeAccountId)
                                .flatMapSingle(incomeAccount -> data
                                        .insert(new Operation()
                                                .setType(OperationType.TRANSFER_INCOME_OPERATION)
                                                .setAccount(incomeAccount)
                                                .setLinkedTransferOperation(transferExpenseOperation)
                                                .setArticle(transferExpenseOperation.getArticle())
                                                .setOwner(transferExpenseOperation.getOwner())
                                                .setExchangeRate(transferExpenseOperation.getExchangeRate())
                                                .setDate(date)
                                                .setValue(value)
                                                .setDescription(description)
                                                .setUrl(url)
                                        )
                                        .flatMap(transferIncomeOperation -> {
                                            transferExpenseOperation.setLinkedTransferOperation(transferIncomeOperation);
                                            return data.update(transferExpenseOperation);
                                        })
                                )
                        )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }

    @Override
    public EntityDestroyer<Operation> createDestroyer(OperationView operation) {
        return new TransferOperationForceDestroyer(context, data);
    }
}
