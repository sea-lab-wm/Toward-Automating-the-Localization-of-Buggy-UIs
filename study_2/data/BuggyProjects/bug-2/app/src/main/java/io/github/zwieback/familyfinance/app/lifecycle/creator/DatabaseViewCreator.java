package io.github.zwieback.familyfinance.app.lifecycle.creator;

import android.util.Log;

import java.sql.Connection;

import io.github.zwieback.familyfinance.business.account.lifecycle.creator.AccountViewCreator;
import io.github.zwieback.familyfinance.business.article.lifecycle.creator.ArticleViewCreator;
import io.github.zwieback.familyfinance.business.currency.lifecycle.creator.CurrencyViewCreator;
import io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.creator.ExchangeRateViewCreator;
import io.github.zwieback.familyfinance.business.operation.lifecycle.creator.OperationViewCreator;
import io.github.zwieback.familyfinance.business.person.lifecycle.creator.PersonViewCreator;
import io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.creator.SmsPatternViewCreator;
import io.github.zwieback.familyfinance.business.template.lifecycle.creator.TemplateViewCreator;
import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityViewCreator;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DatabaseViewCreator {

    private static final String TAG = "DatabaseViewCreator";

    private final Connection connection;

    public DatabaseViewCreator(Connection connection) {
        this.connection = connection;
    }

    public void createViews() {
        createView(new CurrencyViewCreator(connection), onCurrencyViewCreated());
        createView(new ExchangeRateViewCreator(connection), onExchangeRateViewCreated());
        createView(new PersonViewCreator(connection), onPersonViewCreated());
        createView(new AccountViewCreator(connection), onAccountViewCreated());
        createView(new ArticleViewCreator(connection), onArticleViewCreated());
        createView(new OperationViewCreator(connection), onOperationViewCreated());
        createView(new TemplateViewCreator(connection), onTemplateViewCreated());
        createView(new SmsPatternViewCreator(connection), onSmsPatternViewCreated());
    }

    private void createView(EntityViewCreator creator, Consumer<Boolean> onViewCreated) {
        Observable.fromCallable(creator)
                .flatMap(observable -> observable)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(onViewCreated);
    }

    private Consumer<Boolean> onCurrencyViewCreated() {
        return ignoredResult -> logFinishOfCreator(CurrencyViewCreator.class);
    }

    private Consumer<Boolean> onExchangeRateViewCreated() {
        return ignoredResult -> logFinishOfCreator(ExchangeRateViewCreator.class);
    }

    private Consumer<Boolean> onPersonViewCreated() {
        return ignoredResult -> logFinishOfCreator(PersonViewCreator.class);
    }

    private Consumer<Boolean> onAccountViewCreated() {
        return ignoredResult -> logFinishOfCreator(AccountViewCreator.class);
    }

    private Consumer<Boolean> onArticleViewCreated() {
        return ignoredResult -> logFinishOfCreator(ArticleViewCreator.class);
    }

    private Consumer<Boolean> onOperationViewCreated() {
        return ignoredResult -> logFinishOfCreator(OperationViewCreator.class);
    }

    private Consumer<Boolean> onTemplateViewCreated() {
        return ignoredResult -> logFinishOfCreator(TemplateViewCreator.class);
    }

    private Consumer<Boolean> onSmsPatternViewCreated() {
        return ignoredResult -> logFinishOfCreator(SmsPatternViewCreator.class);
    }

    private static void logFinishOfCreator(Class creatorClass) {
        Log.d(TAG, "Creator '" + creatorClass.getSimpleName() + "' is finished");
    }
}
