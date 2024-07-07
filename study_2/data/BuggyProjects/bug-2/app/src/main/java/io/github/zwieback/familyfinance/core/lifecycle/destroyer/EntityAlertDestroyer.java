package io.github.zwieback.familyfinance.core.lifecycle.destroyer;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

abstract class EntityAlertDestroyer<E extends IBaseEntity> extends EntityDestroyer<E> {

    EntityAlertDestroyer(Context context, ReactiveEntityStore<Persistable> data) {
        super(context, data);
    }

    @StringRes
    protected abstract int getAlertResourceId();

    void showAlert(@StringRes int resId) {
        new AlertDialog.Builder(context)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(resId)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setPositiveButton(R.string.button_got_it, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
