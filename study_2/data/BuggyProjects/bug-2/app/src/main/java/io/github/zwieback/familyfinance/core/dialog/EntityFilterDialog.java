package io.github.zwieback.familyfinance.core.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.johnpetitto.validator.Validators;

import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.List;

import io.github.zwieback.familyfinance.app.FamilyFinanceApplication;
import io.github.zwieback.familyfinance.core.dialog.exception.UndefinedArgumentsException;
import io.github.zwieback.familyfinance.core.dialog.exception.UndefinedContextException;
import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import io.github.zwieback.familyfinance.core.listener.EntityFilterListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.core.preference.config.DatabasePrefs;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static io.github.zwieback.familyfinance.util.DateUtils.isTextAnLocalDate;
import static io.github.zwieback.familyfinance.util.DateUtils.stringToLocalDate;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;

public abstract class EntityFilterDialog<F extends EntityFilter, B extends ViewDataBinding>
        extends DialogFragment {

    protected static final String DIALOG_TITLE = "dialogTitle";

    protected B binding;
    protected F filter;
    protected DatabasePrefs databasePrefs;
    private ReactiveEntityStore<Persistable> data;
    private EntityFilterListener<F> listener;

    @SuppressWarnings("unchecked")
    @Override
    public final void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EntityFilterListener) {
            listener = (EntityFilterListener<F>) context;
            data = ((FamilyFinanceApplication) ((Activity) context).getApplication()).getData();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement EntityFilterListener");
        }
        databasePrefs = DatabasePrefs.with(context);
    }

    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        F inputFilter = extractFilter();
        filter = createCopyOfFilter(inputFilter);
        binding = createBinding();
        bind(filter);
        return new AlertDialog.Builder(extractContext())
                .setView(binding.getRoot())
                .setTitle(extractDialogTitle())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // override behavior in #onResume()
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                    // default behavior
                })
                .create();
    }

    @Override
    public final void onResume() {
        super.onResume();
        AlertDialog dialog = (AlertDialog) getDialog();
        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(view -> {
            if (noneErrorFound()) {
                updateFilterProperties();
                listener.onApplyFilter(filter);
                dialog.dismiss();
            }
        });
    }

    private F extractFilter() {
        return extractArguments().getParcelable(getInputFilterName());
    }

    @StringRes
    private int extractDialogTitle() {
        return extractArguments().getInt(DIALOG_TITLE, getDialogTitle());
    }

    @NonNull
    private Bundle extractArguments() {
        if (getArguments() == null) {
            throw new UndefinedArgumentsException();
        }
        return getArguments();
    }

    @NonNull
    private Context extractContext() {
        if (getContext() == null) {
            throw new UndefinedContextException();
        }
        return getContext();
    }

    /**
     * A copy is needed not to overwrite the values of the fields of the original filter.
     *
     * @param filter an input filter
     * @return a copy of input filter
     */
    protected abstract F createCopyOfFilter(F filter);

    protected abstract String getInputFilterName();

    @StringRes
    protected abstract int getDialogTitle();

    @LayoutRes
    protected abstract int getDialogLayoutId();

    private B createBinding() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        return DataBindingUtil.inflate(inflater, getDialogLayoutId(), null, false);
    }

    @CallSuper
    protected void bind(F filter) {
        // stub
    }

    protected final <E extends IBaseEntity> void loadEntity(Class<E> entityClass,
                                                            @NonNull Integer entityId,
                                                            Consumer<E> onSuccess) {
        data.findByKey(entityClass, entityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }

    private boolean noneErrorFound() {
        return Validators.validate(getLayoutsForValidation());
    }

    protected List<ValidatingTextInputLayout> getLayoutsForValidation() {
        return Collections.emptyList();
    }

    protected abstract void updateFilterProperties();

    protected static int extractId(Intent resultIntent, String name) {
        return resultIntent.getIntExtra(name, ID_AS_NULL);
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull
    protected static LocalDate determineDate(EditText dateEdit, @Nullable LocalDate defaultDate) {
        if (isCorrectDate(dateEdit)) {
            return stringToLocalDate(dateEdit.getText().toString());
        }
        if (defaultDate != null) {
            return defaultDate;
        }
        return DateUtils.now();
    }

    private static boolean isCorrectDate(EditText dateEdit) {
        return isTextAnLocalDate(dateEdit.getText().toString());
    }

    protected static <F extends EntityFilter> Bundle createArguments(String filterName, F filter) {
        Bundle args = new Bundle();
        args.putParcelable(filterName, filter);
        return args;
    }
}
