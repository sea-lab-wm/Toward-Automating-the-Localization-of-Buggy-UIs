package io.github.zwieback.familyfinance.business.template.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.template.activity.helper.TemplateQualifier;
import io.github.zwieback.familyfinance.business.template.filter.TemplateFilter;
import io.github.zwieback.familyfinance.business.template.fragment.TemplateFragment;
import io.github.zwieback.familyfinance.business.template.lifecycle.destroyer.TemplateFromSmsPatternsDestroyer;
import io.github.zwieback.familyfinance.business.template.listener.OnTemplateClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Template;
import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.github.zwieback.familyfinance.core.model.type.TemplateType;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_TEMPLATE_ID;
import static io.github.zwieback.familyfinance.business.template.filter.TemplateFilter.TEMPLATE_FILTER;

public class TemplateActivity
        extends EntityActivity<TemplateView, Template, TemplateFilter, TemplateFragment>
        implements OnTemplateClickListener {

    private TemplateQualifier templateQualifier;

    @Override
    protected List<Integer> collectMenuIds() {
        if (!readOnly) {
            return Collections.singletonList(R.menu.menu_entity_template);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_expense_template:
                addExpenseTemplate();
                return true;
            case R.id.action_add_income_template:
                addIncomeTemplate();
                return true;
            case R.id.action_add_transfer_template:
                addTransferTemplate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getTitleStringId() {
        return R.string.template_activity_title;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        templateQualifier = new TemplateQualifier(this, data);
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return TEMPLATE_FILTER;
    }

    @NonNull
    @Override
    protected TemplateFilter createDefaultFilter() {
        return new TemplateFilter();
    }

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_TEMPLATE_ID;
    }

    @Override
    protected String getFragmentTag() {
        return getLocalClassName();
    }

    @Override
    protected TemplateFragment createFragment() {
        return TemplateFragment.newInstance(filter);
    }

    @Override
    public void onEntityClick(View view, TemplateView template) {
        super.onEntityClick(view, template);
        if (regularSelectable) {
            return;
        }
        Intent intent = templateQualifier.determineHelper(template).getIntentToAdd(
                template.getArticleId(), template.getAccountId(), template.getTransferAccountId(),
                template.getOwnerId(), template.getCurrencyId(), template.getExchangeRateId(),
                template.getDate(), template.getValue(), template.getDescription(), template.getUrl());
        startActivity(intent);
    }

    private void addExpenseTemplate() {
        addTemplate(TemplateType.EXPENSE_OPERATION);
    }

    private void addIncomeTemplate() {
        addTemplate(TemplateType.INCOME_OPERATION);
    }

    private void addTransferTemplate() {
        addTemplate(TemplateType.TRANSFER_OPERATION);
    }

    private void addTemplate(TemplateType type) {
        super.addEntity();
        Intent intent = new Intent(this, TemplateEditActivity.class);
        intent.putExtra(TemplateEditActivity.INPUT_TEMPLATE_TYPE, type.name());
        startActivity(intent);
    }

    @Override
    protected void editEntity(TemplateView template) {
        super.editEntity(template);
        Intent intent = new Intent(this, TemplateEditActivity.class);
        intent.putExtra(TemplateEditActivity.INPUT_TEMPLATE_ID, template.getId());
        startActivity(intent);
    }

    @Override
    protected Class<Template> getClassOfRegularEntity() {
        return Template.class;
    }

    @Override
    protected EntityDestroyer<Template> createDestroyer(TemplateView template) {
        return new TemplateFromSmsPatternsDestroyer(this, data);
    }
}
