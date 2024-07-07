package io.github.zwieback.familyfinance.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.johnpetitto.validator.ValidatingTextInputLayout;
import com.johnpetitto.validator.Validators;
import com.mikepenz.iconics.view.IconicsImageView;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.iconics.activity.IconicsActivity;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ICONICS_CODE;
import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.isNullId;

public abstract class EntityEditActivity<E extends IBaseEntity, B extends ViewDataBinding>
        extends DataActivityWrapper {

    private static final String TAG = "EntityEditActivity";

    protected E entity;
    protected B binding;
    protected EntityProvider<E> provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = createProvider();

        int id = extractInputId(getExtraInputId());
        if (isNullId(id)) {
            createEntity();
        } else {
            loadEntity(id);
        }
    }

    @Override
    protected void setupContentView() {
        binding = DataBindingUtil.setContentView(this, getBindingLayoutId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_entity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveEntity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ICONICS_CODE:
                String iconName = resultIntent.getStringExtra(IconicsActivity.OUTPUT_ICON_NAME);
                setupIcon(iconName);
                break;
        }
    }

    public void onSelectIconClick(View view) {
        Intent intent = new Intent(this, IconicsActivity.class);
        startActivityForResult(intent, ICONICS_CODE);
    }

    @LayoutRes
    protected abstract int getBindingLayoutId();

    protected abstract String getExtraInputId();

    protected abstract String getExtraOutputId();

    protected abstract Class<E> getEntityClass();

    protected abstract EntityProvider<E> createProvider();

    protected abstract void createEntity();

    protected void loadEntity(int entityId) {
        loadEntity(getEntityClass(), entityId, this::bind);
    }

    protected final <T extends IBaseEntity> void loadEntity(Class<T> entityClass,
                                                            int entityId,
                                                            Consumer<T> onSuccess) {
        super.loadEntity(entityClass, entityId, onSuccess, Functions.ON_ERROR_MISSING);
    }

    /**
     * Must be called at the end of the method.
     *
     * @param entity new instance entity
     */
    @CallSuper
    protected void bind(E entity) {
        setupBindings();
    }

    protected void setupBindings() {
        // stub
    }

    /**
     * Change icon of entity.
     *
     * @param iconName a new name of icon
     */
    private void setupIcon(String iconName) {
        String oldIconName = entity.getIconName();
        try {
            entity.setIconName(iconName);
            provider.setupIcon(getIconView().getIcon(), entity);
        } catch (IllegalArgumentException | NullPointerException e) {
            entity.setIconName(oldIconName);
            String errorMessage = getResources().getString(R.string.can_not_find_icon, iconName);
            Log.w(TAG, "setupIcon: " + errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    protected abstract IconicsImageView getIconView();

    // -----------------------------------------------------------------------------------------
    // Saving
    // -----------------------------------------------------------------------------------------

    private void saveEntity() {
        if (anyErrorFound()) {
            return;
        }
        updateEntityProperties(entity);
        saveEntity(entity, onSuccessfulSaving());
    }

    private boolean anyErrorFound() {
        return !Validators.validate(getLayoutsForValidation());
    }

    protected abstract List<ValidatingTextInputLayout> getLayoutsForValidation();

    protected abstract void updateEntityProperties(E entity);

    protected final void saveEntity(E entity, Consumer<E> onSuccessfulSaving) {
        Single<E> single = entity.getId() == 0 ? data.insert(entity) : data.update(entity);
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccessfulSaving);
    }

    protected Consumer<E> onSuccessfulSaving() {
        return this::closeActivity;
    }

    // -----------------------------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------------------------

    protected final void closeActivity(E entity) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(getExtraOutputId(), entity.getId());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    protected final int extractInputId(@NonNull String name) {
        return extractInputId(name, ID_AS_NULL);
    }

    protected final int extractInputId(@NonNull String name, int defaultValue) {
        return getIntent().getIntExtra(name, defaultValue);
    }

    @NonNull
    protected final LocalDate extractInputDate(@NonNull String name) {
        return DateUtils.readLocalDateFromIntent(getIntent(), name);
    }

    protected final BigDecimal extractInputBigDecimal(@NonNull String name) {
        return NumberUtils.readBigDecimalFromIntent(getIntent(), name);
    }

    @Nullable
    protected final String extractInputString(@NonNull String name) {
        return getIntent().getStringExtra(name);
    }

    protected final void disableLayout(@NonNull TextInputLayout layout, @StringRes int hintId) {
        layout.setEnabled(false);
        layout.setHint(getResources().getString(hintId));
    }

    protected final void disableLayout(@NonNull LinearLayout layout) {
        layout.setEnabled(false);
    }

    protected static int extractOutputId(@NonNull Intent resultIntent, @NonNull String name) {
        return resultIntent.getIntExtra(name, ID_AS_NULL);
    }
}
