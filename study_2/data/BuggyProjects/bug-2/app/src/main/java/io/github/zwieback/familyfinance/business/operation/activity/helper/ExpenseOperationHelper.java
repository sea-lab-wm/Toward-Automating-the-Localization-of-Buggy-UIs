package io.github.zwieback.familyfinance.business.operation.activity.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.business.operation.activity.ExpenseOperationEditActivity;
import io.github.zwieback.familyfinance.business.operation.filter.ExpenseOperationFilter;
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

public class ExpenseOperationHelper extends OperationHelper<ExpenseOperationFilter> {

    public ExpenseOperationHelper(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @Override
    public Intent getIntentToAdd() {
        return getEmptyIntent();
    }

    @Override
    public Intent getIntentToAdd(@Nullable Integer articleId,
                                 @Nullable Integer accountId,
                                 @Nullable Integer ignoredTransferAccountId,
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
                articleId,
                accountId, ignoredTransferAccountId,
                ownerId,
                currencyId, exchangeRateId,
                date, value,
                description, url
        );
    }

    @NonNull
    @Override
    Intent getIntentToAdd(@NonNull Intent preparedIntent,
                          @Nullable Integer articleId,
                          @Nullable Integer accountId,
                          @Nullable Integer ignoredTransferAccountId,
                          @Nullable Integer ownerId,
                          @Nullable Integer currencyId,
                          @Nullable Integer exchangeRateId,
                          @Nullable LocalDate date,
                          @Nullable BigDecimal value,
                          @Nullable String description,
                          @Nullable String url) {
        if (articleId != null && articleId != databasePrefs.getExpensesArticleId()) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_ARTICLE_ID, articleId);
        }
        if (accountId != null) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_ACCOUNT_ID, accountId);
        }
        if (ownerId != null) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_OWNER_ID, ownerId);
        }
        if (currencyId != null) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_CURRENCY_ID, currencyId);
        }
        if (exchangeRateId != null) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_EXCHANGE_RATE_ID,
                    exchangeRateId);
        }
        if (date != null) {
            DateUtils.writeLocalDateToIntent(preparedIntent,
                    ExpenseOperationEditActivity.INPUT_EXPENSE_DATE, date);
        }
        if (value != null) {
            NumberUtils.writeBigDecimalToIntent(preparedIntent,
                    ExpenseOperationEditActivity.INPUT_EXPENSE_VALUE, value);
        }
        if (StringUtils.isTextNotEmpty(description)) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_DESCRIPTION, description);
        }
        if (StringUtils.isTextNotEmpty(url)) {
            preparedIntent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_URL, url);
        }
        return preparedIntent;
    }

    @Override
    public Intent getIntentToAdd(@Nullable ExpenseOperationFilter filter) {
        if (filter == null) {
            return getEmptyIntent();
        }
        return getIntentToAdd(filter.getArticleId(), filter.getAccountId(), null,
                filter.getOwnerId(), filter.getCurrencyId(), null, null, null, null, null);
    }

    @Override
    public Intent getIntentToEdit(OperationView operation) {
        Intent intent = getEmptyIntent();
        intent.putExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_OPERATION_ID, operation.getId());
        return intent;
    }

    @Override
    public Intent getIntentToDuplicate(OperationView operation) {
        return getIntentToAdd(operation.getArticleId(), operation.getAccountId(), null,
                operation.getOwnerId(), operation.getCurrencyId(), operation.getExchangeRateId(),
                operation.getDate(), operation.getValue(), operation.getDescription(), operation.getUrl());
    }

    @Override
    Intent getEmptyIntent() {
        return new Intent(context, ExpenseOperationEditActivity.class);
    }

    @Override
    public boolean validToAddImmediately(@Nullable Integer articleId,
                                         @Nullable Integer accountId,
                                         @Nullable Integer ignoredTransferAccountId,
                                         @Nullable Integer ownerId,
                                         @Nullable Integer currencyId,
                                         @Nullable Integer exchangeRateId,
                                         @Nullable LocalDate date,
                                         @Nullable BigDecimal value,
                                         @Nullable String description,
                                         @Nullable String url) {
        return articleId != null
                && accountId != null
                && ownerId != null
                && exchangeRateId != null
                && date != null
                && value != null;
    }

    @NonNull
    @Override
    public Intent getIntentToAddImmediately(@Nullable Integer articleId,
                                            @Nullable Integer accountId,
                                            @Nullable Integer ignoredTransferAccountId,
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
                articleId,
                accountId, ignoredTransferAccountId,
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
                .putExtra(INPUT_OPERATION_TYPE, OperationType.EXPENSE_OPERATION);
    }

    @NonNull
    @Override
    public Disposable addOperationImmediately(@NonNull Intent intent, @NonNull Consumer<Operation> onSuccess) {
        int accountId = intent.getIntExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_ACCOUNT_ID, 0);
        int articleId = intent.getIntExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_ARTICLE_ID, 0);
        int ownerId = intent.getIntExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_OWNER_ID, 0);
        int exchangeRateId = intent.getIntExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_EXCHANGE_RATE_ID, 0);
        LocalDate date = DateUtils.readLocalDateFromIntent(intent, ExpenseOperationEditActivity.INPUT_EXPENSE_DATE);
        BigDecimal value = NumberUtils.readBigDecimalFromIntent(intent, ExpenseOperationEditActivity.INPUT_EXPENSE_VALUE);
        String description = intent.getStringExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_DESCRIPTION);
        String url = intent.getStringExtra(ExpenseOperationEditActivity.INPUT_EXPENSE_URL);
        return Maybe.zip(
                data.findByKey(Account.class, accountId),
                data.findByKey(Article.class, articleId),
                data.findByKey(Person.class, ownerId),
                data.findByKey(ExchangeRate.class, exchangeRateId),
                (account, article, owner, exchangeRate) ->
                        new Operation()
                                .setType(OperationType.EXPENSE_OPERATION)
                                .setAccount(account)
                                .setArticle(article)
                                .setOwner(owner)
                                .setExchangeRate(exchangeRate)
                                .setDate(date)
                                .setValue(value)
                                .setDescription(description)
                                .setUrl(url)
        )
                .flatMapSingle(data::insert)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }
}
