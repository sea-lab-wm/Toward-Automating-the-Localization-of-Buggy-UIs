package io.github.zwieback.familyfinance.business.operation.activity.helper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.business.operation.activity.IncomeOperationEditActivity;
import io.github.zwieback.familyfinance.business.operation.filter.IncomeOperationFilter;
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

public class IncomeOperationHelper extends OperationHelper<IncomeOperationFilter> {

    public IncomeOperationHelper(Context context, ReactiveEntityStore<Persistable> data) {
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
        if (articleId != null && articleId != databasePrefs.getIncomesArticleId()) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_ARTICLE_ID, articleId);
        }
        if (accountId != null) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_ACCOUNT_ID, accountId);
        }
        if (ownerId != null) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_OWNER_ID, ownerId);
        }
        if (currencyId != null) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_CURRENCY_ID, currencyId);
        }
        if (exchangeRateId != null) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_EXCHANGE_RATE_ID,
                    exchangeRateId);
        }
        if (date != null) {
            DateUtils.writeLocalDateToIntent(preparedIntent,
                    IncomeOperationEditActivity.INPUT_INCOME_DATE, date);
        }
        if (value != null) {
            NumberUtils.writeBigDecimalToIntent(preparedIntent,
                    IncomeOperationEditActivity.INPUT_INCOME_VALUE, value);
        }
        if (StringUtils.isTextNotEmpty(description)) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_DESCRIPTION, description);
        }
        if (StringUtils.isTextNotEmpty(url)) {
            preparedIntent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_URL, url);
        }
        return preparedIntent;
    }

    @Override
    public Intent getIntentToAdd(@Nullable IncomeOperationFilter filter) {
        if (filter == null) {
            return getEmptyIntent();
        }
        return getIntentToAdd(filter.getArticleId(), filter.getAccountId(), null,
                filter.getOwnerId(), filter.getCurrencyId(), null, null, null, null, null);
    }

    @Override
    public Intent getIntentToEdit(OperationView operation) {
        Intent intent = getEmptyIntent();
        intent.putExtra(IncomeOperationEditActivity.INPUT_INCOME_OPERATION_ID, operation.getId());
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
        return new Intent(context, IncomeOperationEditActivity.class);
    }

    @Override
    public boolean validToAddImmediately(@Nullable Integer articleId,
                                         @Nullable Integer accountId,
                                         @Nullable Integer transferAccountId,
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
                .putExtra(INPUT_OPERATION_TYPE, OperationType.INCOME_OPERATION);
    }

    @NonNull
    @Override
    public Disposable addOperationImmediately(@NonNull Intent intent, @NonNull Consumer<Operation> onSuccess) {
        int accountId = intent.getIntExtra(IncomeOperationEditActivity.INPUT_INCOME_ACCOUNT_ID, 0);
        int articleId = intent.getIntExtra(IncomeOperationEditActivity.INPUT_INCOME_ARTICLE_ID, 0);
        int ownerId = intent.getIntExtra(IncomeOperationEditActivity.INPUT_INCOME_OWNER_ID, 0);
        int exchangeRateId = intent.getIntExtra(IncomeOperationEditActivity.INPUT_INCOME_EXCHANGE_RATE_ID, 0);
        LocalDate date = DateUtils.readLocalDateFromIntent(intent, IncomeOperationEditActivity.INPUT_INCOME_DATE);
        BigDecimal value = NumberUtils.readBigDecimalFromIntent(intent, IncomeOperationEditActivity.INPUT_INCOME_VALUE);
        String description = intent.getStringExtra(IncomeOperationEditActivity.INPUT_INCOME_DESCRIPTION);
        String url = intent.getStringExtra(IncomeOperationEditActivity.INPUT_INCOME_URL);
        return Maybe.zip(
                data.findByKey(Account.class, accountId),
                data.findByKey(Article.class, articleId),
                data.findByKey(Person.class, ownerId),
                data.findByKey(ExchangeRate.class, exchangeRateId),
                (account, article, owner, exchangeRate) ->
                        new Operation()
                                .setType(OperationType.INCOME_OPERATION)
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
