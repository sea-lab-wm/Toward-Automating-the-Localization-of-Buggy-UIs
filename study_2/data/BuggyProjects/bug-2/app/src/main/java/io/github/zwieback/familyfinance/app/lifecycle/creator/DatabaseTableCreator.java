package io.github.zwieback.familyfinance.app.lifecycle.creator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.Stream;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.account.lifecycle.creator.AccountCreator;
import io.github.zwieback.familyfinance.business.article.lifecycle.creator.ArticleEntriesCreator;
import io.github.zwieback.familyfinance.business.article.lifecycle.creator.ArticleFoldersCreator;
import io.github.zwieback.familyfinance.business.article.lifecycle.creator.ArticleRootCreator;
import io.github.zwieback.familyfinance.business.article.lifecycle.creator.exception.NoArticleFoundException;
import io.github.zwieback.familyfinance.business.currency.lifecycle.creator.CurrencyCreator;
import io.github.zwieback.familyfinance.business.exchange_rate.lifecycle.creator.ExchangeRateCreator;
import io.github.zwieback.familyfinance.business.person.lifecycle.creator.PersonCreator;
import io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.creator.SmsPatternCreator;
import io.github.zwieback.familyfinance.business.template.lifecycle.creator.TemplateCreator;
import io.github.zwieback.familyfinance.core.lifecycle.creator.EntityCreator;
import io.github.zwieback.familyfinance.core.model.Account;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.Currency;
import io.github.zwieback.familyfinance.core.model.ExchangeRate;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.core.model.Person;
import io.github.zwieback.familyfinance.core.model.SmsPattern;
import io.github.zwieback.familyfinance.core.model.Template;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public class DatabaseTableCreator {

    private static final String TAG = "DatabaseTableCreator";

    @NonNull
    private final Context context;
    @NonNull
    private final ReactiveEntityStore<Persistable> data;
    @NonNull
    private final DatabasePrefs databasePrefs;

    public DatabaseTableCreator(@NonNull Context context,
                                @NonNull ReactiveEntityStore<Persistable> data) {
        this.context = context;
        this.data = data;
        this.databasePrefs = DatabasePrefs.with(context);
    }

    public void createTables() {
        CurrencyCreator currencyCreator = new CurrencyCreator(context, data);
        createTable(currencyCreator, onCurrenciesCreated());
    }

    private <E extends IBaseEntity> void createTable(EntityCreator<E> creator,
                                                     Consumer<Iterable<E>> onEntitiesCreated) {
        Observable.fromCallable(creator)
                .flatMap(observable -> observable)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(onEntitiesCreated);
    }

    private Consumer<Iterable<Currency>> onCurrenciesCreated() {
        return currencies -> {
            Currency currency = currencies.iterator().next();
            databasePrefs.setCurrencyId(currency.getId());
            logFinishOfCreator(CurrencyCreator.class);

            createTable(new ExchangeRateCreator(context, data), onExchangeRatesCreated());
            createTable(new PersonCreator(context, data), onPeopleCreated());
        };
    }

    private Consumer<Iterable<ExchangeRate>> onExchangeRatesCreated() {
        return exchangeRates -> logFinishOfCreator(ExchangeRateCreator.class);
    }

    private Consumer<Iterable<Person>> onPeopleCreated() {
        return people -> {
            Person chief = people.iterator().next();
            databasePrefs.setPersonId(chief.getId());
            logFinishOfCreator(PersonCreator.class);

            createTable(new AccountCreator(context, data), onAccountsCreated());
            createTable(new ArticleRootCreator(context, data), onArticleRootsCreated());
        };
    }

    private Consumer<Iterable<Account>> onAccountsCreated() {
        return accounts -> {
            Account account = accounts.iterator().next();
            databasePrefs.setAccountId(account.getId());
            logFinishOfCreator(AccountCreator.class);
        };
    }

    private Consumer<Iterable<Article>> onArticleRootsCreated() {
        return articles -> {
            String incomesName = context.getResources().getString(R.string.article_incomes);
            String expensesName = context.getResources().getString(R.string.article_expenses);

            Article incomesArticle = findArticle(articles, incomesName);
            Article expensesArticle = findArticle(articles, expensesName);
            databasePrefs.setIncomesArticleId(incomesArticle.getId());
            databasePrefs.setExpensesArticleId(expensesArticle.getId());
            logFinishOfCreator(ArticleRootCreator.class);

            createTable(new ArticleFoldersCreator(context, data), onArticleFoldersCreated());
        };
    }

    private Consumer<Iterable<Article>> onArticleFoldersCreated() {
        return articles -> {
            logFinishOfCreator(ArticleFoldersCreator.class);

            createTable(new ArticleEntriesCreator(context, data), onArticleEntriesCreated());
        };
    }

    private Consumer<Iterable<Article>> onArticleEntriesCreated() {
        return articles -> {
            String transferName = context.getResources().getString(R.string.article_transfer);

            Article transferArticle = findArticle(articles, transferName);
            databasePrefs.setTransferArticleId(transferArticle.getId());

            logFinishOfCreator(ArticleEntriesCreator.class);

            createTable(new TemplateCreator(context, data), onTemplatesCreated());
        };
    }

    private Consumer<Iterable<Template>> onTemplatesCreated() {
        return articles -> {
            logFinishOfCreator(TemplateCreator.class);

            createTable(new SmsPatternCreator(context, data), onSmsPatternCreated());
        };
    }

    private Consumer<Iterable<SmsPattern>> onSmsPatternCreated() {
        return smsPatterns -> {
            logFinishOfCreator(SmsPatternCreator.class);

            Log.d(TAG, "Tables were created");
        };
    }

    @NonNull
    private static Article findArticle(Iterable<Article> articles, @NonNull String name) {
        return Stream.of(articles)
                .filter(article -> name.equals(article.getName()))
                .findFirst()
                .orElseThrow(() -> new NoArticleFoundException(name));
    }

    private static void logFinishOfCreator(Class creatorClass) {
        Log.d(TAG, "Creator '" + creatorClass.getSimpleName() + "' is finished");
    }
}
