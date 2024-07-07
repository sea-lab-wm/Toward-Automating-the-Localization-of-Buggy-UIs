package io.github.zwieback.familyfinance.core.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.annimon.stream.Objects;

import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.filter.EntityFolderFilter;
import io.github.zwieback.familyfinance.core.fragment.EntityFolderFragment;
import io.github.zwieback.familyfinance.core.listener.EntityFolderClickListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntityFolder;

import static io.github.zwieback.familyfinance.util.NumberUtils.integerToIntId;

public abstract class EntityFolderActivity<
        ENTITY extends IBaseEntityFolder,
        REGULAR_ENTITY extends IBaseEntityFolder,
        FILTER extends EntityFolderFilter,
        FRAGMENT extends EntityFolderFragment>
        extends EntityActivity<ENTITY, REGULAR_ENTITY, FILTER, FRAGMENT>
        implements EntityFolderClickListener<ENTITY> {

    public static final String INPUT_PROHIBITED_FOLDER_ID = "inputProhibitedFolderId";
    public static final String INPUT_FOLDER_SELECTABLE = "inputFolderSelectable";

    @Nullable
    private Integer prohibitedFolderId;
    private boolean folderSelectable;

    // -----------------------------------------------------------------------------------------
    // Menu methods
    // -----------------------------------------------------------------------------------------

    @Override
    protected List<Integer> collectMenuIds() {
        List<Integer> menuIds = new ArrayList<>(super.collectMenuIds());
        if (readOnly) {
            menuIds.add(R.menu.menu_entity_folder_read_only);
        } else {
            menuIds.add(R.menu.menu_entity_folder);
        }
        return menuIds;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_folder:
                addFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // -----------------------------------------------------------------------------------------
    // Init methods
    // -----------------------------------------------------------------------------------------

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        prohibitedFolderId = extractId(getIntent().getExtras(), INPUT_PROHIBITED_FOLDER_ID);
        folderSelectable = extractBoolean(getIntent().getExtras(), INPUT_FOLDER_SELECTABLE, true);
    }

    // -----------------------------------------------------------------------------------------
    // Fragment methods
    // -----------------------------------------------------------------------------------------

    @Override
    protected boolean isFirstFrame() {
        return filter.getParentId() == null;
    }

    // -----------------------------------------------------------------------------------------
    // Menu and click methods
    // -----------------------------------------------------------------------------------------

    @Override
    public void onFolderClick(View view, ENTITY entity) {
        if (accessAllowed(entity)) {
            filter.setParentId(entity.getId());
            replaceFragment(true);
        } else {
            showAccessDeniedToast();
        }
    }

    @Override
    public void onFolderLongClick(View view, ENTITY entity) {
        if (accessAllowed(entity)) {
            showPopup(view, entity);
        } else {
            showAccessDeniedToast();
        }
    }

    private boolean accessAllowed(ENTITY entity) {
        return !Objects.equals(entity.getId(), prohibitedFolderId);
    }

    @Override
    protected int getPopupMenuId(ENTITY entity) {
        if (entity.isFolder()) {
            if (readOnly) {
                if (folderSelectable) {
                    return R.menu.popup_entity_folder_select;
                }
                return R.menu.popup_entity_folder_read_only;
            }
            return R.menu.popup_entity_folder;
        }
        return super.getPopupMenuId(entity);
    }

    @Override
    protected PopupMenu.OnMenuItemClickListener getPopupItemClickListener(ENTITY entity) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.action_select:
                    closeActivity(entity);
                    return true;
                case R.id.action_add_nested_entry:
                    addNestedEntity(entity);
                    return true;
                case R.id.action_add_nested_folder:
                    addNestedFolder(entity);
                    return true;
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

    @Override
    protected void addEntity() {
        super.addEntity();
        addEntity(determineValidParentId(), false);
    }

    protected void addFolder() {
        addEntity(determineValidParentId(), true);
    }

    protected void addNestedEntity(ENTITY entity) {
        addEntity(entity.getId(), false);
    }

    protected void addNestedFolder(ENTITY entity) {
        addEntity(entity.getId(), true);
    }

    private int determineValidParentId() {
        return integerToIntId(findFragment().getParentId());
    }

    @CallSuper
    protected void addEntity(int parentId, boolean isFolder) {
        checkReadOnly();
    }

    // -----------------------------------------------------------------------------------------
    // Helper methods
    // -----------------------------------------------------------------------------------------

    private void showAccessDeniedToast() {
        Toast.makeText(this, R.string.access_to_this_folder_is_denied, Toast.LENGTH_SHORT).show();
    }
}
