package io.github.zwieback.familyfinance.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.annimon.stream.Stream;
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.activity.exception.ReadOnlyException;
import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import io.github.zwieback.familyfinance.core.fragment.EntityFragment;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.listener.EntityClickListener;
import io.github.zwieback.familyfinance.core.listener.EntityFilterListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;

import static io.github.zwieback.familyfinance.util.NumberUtils.ID_AS_NULL;
import static io.github.zwieback.familyfinance.util.NumberUtils.intToIntegerId;

public abstract class EntityActivity<
        ENTITY extends IBaseEntity,
        REGULAR_ENTITY extends IBaseEntity,
        FILTER extends EntityFilter,
        FRAGMENT extends EntityFragment>
        extends DataActivityWrapper
        implements EntityClickListener<ENTITY>, EntityFilterListener<FILTER> {

    public static final String INPUT_READ_ONLY = "inputReadOnly";
    public static final String INPUT_REGULAR_SELECTABLE = "inputRegularSelectable";

    protected FILTER filter;
    protected boolean readOnly;
    protected boolean regularSelectable;
    private boolean filterWasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        replaceFragment(!isFirstFrame());
    }

    @Override
    protected void setupContentView() {
        setContentView(getContentLayoutId());
    }

    // -----------------------------------------------------------------------------------------
    // Menu methods
    // -----------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        List<Integer> menuIds = new ArrayList<>(collectMenuIds());
        if (addFilterMenuItem()) {
            menuIds.add(R.menu.menu_entity_filter);
        }
        Stream.of(menuIds)
                .forEach(menuId -> IconicsMenuInflaterUtil.inflate(inflater, this, menuId, menu));
        return super.onCreateOptionsMenu(menu);
    }

    protected boolean addFilterMenuItem() {
        return false;
    }

    protected List<Integer> collectMenuIds() {
        return readOnly
                ? Collections.singletonList(R.menu.menu_entity_read_only)
                : Collections.singletonList(R.menu.menu_entity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilterDialog();
                return true;
            case R.id.action_add_entry:
                addEntity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // -----------------------------------------------------------------------------------------
    // Init methods
    // -----------------------------------------------------------------------------------------

    @LayoutRes
    protected int getContentLayoutId() {
        return R.layout.activity_entity;
    }

    @CallSuper
    protected void init(Bundle savedInstanceState) {
        readOnly = extractBoolean(getIntent().getExtras(), INPUT_READ_ONLY, true);
        regularSelectable = extractBoolean(getIntent().getExtras(), INPUT_REGULAR_SELECTABLE, true);
        filter = extractFilter(getIntent().getExtras(), getFilterName());
        filter = loadFilter(savedInstanceState, filter);
    }

    // -----------------------------------------------------------------------------------------
    // Filter methods
    // -----------------------------------------------------------------------------------------

    @NonNull
    protected abstract String getFilterName();

    private FILTER loadFilter(Bundle savedInstanceState, FILTER filter) {
        if (savedInstanceState == null) {
            if (filter == null) {
                return createDefaultFilter();
            }
            return filter;
        }
        return savedInstanceState.getParcelable(getFilterName());
    }

    @NonNull
    protected abstract FILTER createDefaultFilter();

    @Override
    protected void onStart() {
        super.onStart();
        applyFilter(filter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(getFilterName(), filter);
    }

    @Override
    public void onApplyFilter(FILTER filter) {
        if (!this.filter.equals(filter)) {
            filterWasChanged = true;
        }
        this.filter = filter;
        applyFilter(filter);
    }

    @SuppressWarnings("unchecked")
    protected final void applyFilter(FILTER filter) {
        FRAGMENT fragment = findFragment();
        if (fragment != null) {
            fragment.applyFilter(filter);
        }
    }

    @Override
    public void onBackPressed() {
        if (needToReturnFilter()) {
            closeActivity(null);
        }
        super.onBackPressed();
    }

    private boolean needToReturnFilter() {
        return filterWasChanged && getSupportFragmentManager().getBackStackEntryCount() == 0;
    }

    protected void showFilterDialog() {
        // stub
    }

    // -----------------------------------------------------------------------------------------
    // Fragment methods
    // -----------------------------------------------------------------------------------------

    protected boolean isFirstFrame() {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected final void replaceFragment(boolean addToBackStack) {
        String tag = getFragmentTag();
        FRAGMENT fragment = (FRAGMENT) getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = createFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(getFragmentContainerId(), fragment, tag);
            if (addToBackStack) {
                transaction.addToBackStack(null);
            }
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            transaction.commit();
        }
    }

    protected abstract String getFragmentTag();

    protected abstract FRAGMENT createFragment();

    @IdRes
    protected int getFragmentContainerId() {
        return R.id.entity_fragment;
    }

    private void refresh() {
        FRAGMENT fragment = findFragment();
        if (fragment != null) {
            fragment.refresh();
        }
    }

    @SuppressWarnings("unchecked")
    protected final FRAGMENT findFragment() {
        return (FRAGMENT) getSupportFragmentManager().findFragmentById(getFragmentContainerId());
    }

    // -----------------------------------------------------------------------------------------
    // Menu and click methods
    // -----------------------------------------------------------------------------------------

    @Override
    public void onEntityClick(View view, ENTITY entity) {
        if (regularSelectable) {
            closeActivity(entity);
        }
    }

    @Override
    public void onEntityLongClick(View view, ENTITY entity) {
        showPopup(view, entity);
    }

    final void showPopup(View view, ENTITY entity) {
        PopupMenu popup = new PopupMenu(this, view);
        IconicsMenuInflaterUtil.inflate(popup.getMenuInflater(), this, getPopupMenuId(entity),
                popup.getMenu());
        popup.setOnMenuItemClickListener(getPopupItemClickListener(entity));
        popup.show();
    }

    @MenuRes
    protected int getPopupMenuId(ENTITY entity) {
        if (readOnly) {
            return R.menu.popup_entity_read_only;
        }
        return R.menu.popup_entity;
    }

    protected PopupMenu.OnMenuItemClickListener getPopupItemClickListener(ENTITY entity) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.action_edit:
                    editEntity(entity);
                    return true;
                case R.id.action_delete:
                    deleteEntity(entity);
                    return true;
                default:
                    return false;
            }
        };
    }

    @CallSuper
    protected void addEntity() {
        checkReadOnly();
    }

    @CallSuper
    protected void editEntity(ENTITY entity) {
        checkReadOnly();
    }

    @CallSuper
    protected void duplicateEntity(ENTITY entity) {
        checkReadOnly();
    }

    protected final void deleteEntity(ENTITY entity) {
        checkReadOnly();
        REGULAR_ENTITY regularEntity = findRegularEntity(entity);
        EntityDestroyer<REGULAR_ENTITY> destroyer = createDestroyer(entity);
        destroyer.destroy(regularEntity, deletedEntityCount -> refresh());
    }

    final void checkReadOnly() {
        if (readOnly) {
            throw new ReadOnlyException();
        }
    }

    private REGULAR_ENTITY findRegularEntity(ENTITY entity) {
        return data.findByKey(getClassOfRegularEntity(), entity.getId()).blockingGet();
    }

    protected abstract Class<REGULAR_ENTITY> getClassOfRegularEntity();

    protected abstract EntityDestroyer<REGULAR_ENTITY> createDestroyer(ENTITY entity);

    // -----------------------------------------------------------------------------------------
    // Close methods
    // -----------------------------------------------------------------------------------------

    protected final void closeActivity(@Nullable ENTITY entity) {
        Intent resultIntent = new Intent();
        if (entity != null) {
            resultIntent.putExtra(getResultName(), entity.getId());
        }
        addExtrasIntoResultIntent(resultIntent);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    protected void addExtrasIntoResultIntent(Intent resultIntent) {
        if (needToReturnFilter()) {
            resultIntent.putExtra(getFilterName(), filter);
        }
    }

    @NonNull
    protected abstract String getResultName();

    // -----------------------------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------------------------

    @Nullable
    protected static Integer extractId(@Nullable Bundle bundle, @NonNull String key) {
        if (bundle == null) {
            return null;
        }
        int id = bundle.getInt(key, ID_AS_NULL);
        return intToIntegerId(id);
    }

    protected static boolean extractBoolean(@Nullable Bundle bundle,
                                            @NonNull String key,
                                            boolean defaultValue) {
        if (bundle == null) {
            return defaultValue;
        }
        return bundle.getBoolean(key, defaultValue);
    }

    @Nullable
    private static <FILTER extends EntityFilter> FILTER extractFilter(@Nullable Bundle bundle,
                                                                      @NonNull String key) {
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelable(key);
    }
}
