package io.github.zwieback.familyfinance.app.lifecycle.destroyer;

import android.util.Log;

import java.sql.Connection;

import io.github.zwieback.familyfinance.business.account.lifecycle.destroyer.AccountViewDestroyer;
import io.github.zwieback.familyfinance.business.operation.lifecycle.destroyer.OperationViewDestroyer;
import io.github.zwieback.familyfinance.business.sms_pattern.lifecycle.destroyer.SmsPatternViewDestroyer;
import io.github.zwieback.familyfinance.business.template.lifecycle.destroyer.TemplateViewDestroyer;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityViewDestroyer;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class DatabaseViewDestroyer {

    private static final String TAG = "DatabaseViewDestroyer";

    private final Connection connection;

    public DatabaseViewDestroyer(Connection connection) {
        this.connection = connection;
    }

    public void destroyViews() {
        destroyView(new OperationViewDestroyer(connection));
        destroyView(new AccountViewDestroyer(connection));
        destroyView(new TemplateViewDestroyer(connection));
        destroyView(new SmsPatternViewDestroyer(connection));
    }

    private <T extends EntityViewDestroyer> void destroyView(T destroyer) {
        Observable.fromCallable(destroyer)
                .flatMap(observable -> observable)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(ignoredResult -> logFinishOfDestroyer(destroyer.getClass()));
    }

    private static void logFinishOfDestroyer(Class<?> destroyerClass) {
        Log.d(TAG, "Destroyer '" + destroyerClass.getSimpleName() + "' is finished");
    }
}
