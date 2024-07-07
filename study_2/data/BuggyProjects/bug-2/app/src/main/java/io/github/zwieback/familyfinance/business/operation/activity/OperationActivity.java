package io.github.zwieback.familyfinance.business.operation.activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.business.operation.fragment.OperationFragment;
import io.github.zwieback.familyfinance.business.operation.listener.OnOperationClickListener;
import io.github.zwieback.familyfinance.business.operation.listener.OperationFilterListener;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.model.Operation;
import io.github.zwieback.familyfinance.core.model.OperationView;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_OPERATION_ID;

abstract class OperationActivity<
        FRAGMENT extends OperationFragment<FILTER>,
        FILTER extends OperationFilter>
        extends EntityActivity<OperationView, Operation, FILTER, FRAGMENT>
        implements OnOperationClickListener, OperationFilterListener<FILTER> {

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_OPERATION_ID;
    }

    @Override
    protected boolean addFilterMenuItem() {
        return true;
    }

    @Override
    protected String getFragmentTag() {
        return getLocalClassName();
    }

    @Override
    public void onEntityClick(View view, OperationView entity) {
        // do nothing
    }

    @Override
    protected int getPopupMenuId(OperationView entity) {
        if (readOnly) {
            return super.getPopupMenuId(entity);
        }
        return R.menu.popup_entity_operation;
    }

    @Override
    protected PopupMenu.OnMenuItemClickListener getPopupItemClickListener(OperationView operation) {
        return item -> {
            switch (item.getItemId()) {
                case R.id.action_duplicate:
                    duplicateEntity(operation);
                    return true;
                case R.id.action_edit:
                    editEntity(operation);
                    return true;
                case R.id.action_delete:
                    deleteEntity(operation);
                    return true;
                default:
                    return false;
            }
        };
    }

    @Override
    protected Class<Operation> getClassOfRegularEntity() {
        return Operation.class;
    }
}
