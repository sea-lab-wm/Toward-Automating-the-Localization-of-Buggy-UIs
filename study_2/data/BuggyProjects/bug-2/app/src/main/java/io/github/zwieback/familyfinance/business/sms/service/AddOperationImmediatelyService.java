package io.github.zwieback.familyfinance.business.sms.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import io.github.zwieback.familyfinance.app.FamilyFinanceApplication;
import io.github.zwieback.familyfinance.business.operation.activity.exception.IllegalOperationTypeException;
import io.github.zwieback.familyfinance.business.operation.activity.helper.ExpenseOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.IncomeOperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.OperationHelper;
import io.github.zwieback.familyfinance.business.operation.activity.helper.TransferOperationHelper;
import io.github.zwieback.familyfinance.core.model.type.OperationType;
import io.reactivex.disposables.CompositeDisposable;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static io.github.zwieback.familyfinance.business.sms.common.SmsConst.SMS_NOTIFICATION_ID;

public class AddOperationImmediatelyService extends Service {

    private static final String TAG = "AddOpImmService";

    private IncomeOperationHelper incomeOperationHelper;
    private ExpenseOperationHelper expenseOperationHelper;
    private TransferOperationHelper transferOperationHelper;
    private CompositeDisposable compositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        FamilyFinanceApplication application = (FamilyFinanceApplication) getApplication();
        ReactiveEntityStore<Persistable> data = application.getData();
        incomeOperationHelper = new IncomeOperationHelper(this, data);
        expenseOperationHelper = new ExpenseOperationHelper(this, data);
        transferOperationHelper = new TransferOperationHelper(this, data);
        compositeDisposable = new CompositeDisposable();
        Log.e(TAG, "Service created");
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Service destroyed");
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            Log.e(TAG, "extras: " + extras);
            for (String key : extras.keySet()) {
                Log.e(TAG, "extra[" + key + "] = " + extras.get(key));
            }
            OperationType operationType = (OperationType) extras.getSerializable(OperationHelper.INPUT_OPERATION_TYPE);
            if (operationType == null) {
                throw new IllegalArgumentException("INPUT_OPERATION_TYPE extra must be not null");
            }
            OperationHelper<?> operationHelper = determineOperationHelper(operationType);
            compositeDisposable.add(
                    operationHelper.addOperationImmediately(
                            intent,
                            operation -> {
                                closeNotification(this);
//                                stopSelf();
                            }
                    )
            );
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private static void closeNotification(@NonNull Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(SMS_NOTIFICATION_ID);
        }
    }

    @NonNull
    private OperationHelper<?> determineOperationHelper(@NonNull OperationType operationType) {
        switch (operationType) {
            case EXPENSE_OPERATION:
                return expenseOperationHelper;
            case INCOME_OPERATION:
                return incomeOperationHelper;
            case TRANSFER_EXPENSE_OPERATION:
            case TRANSFER_INCOME_OPERATION:
                return transferOperationHelper;
            default:
                throw IllegalOperationTypeException.unsupportedOperationType(operationType);
        }
    }
}
