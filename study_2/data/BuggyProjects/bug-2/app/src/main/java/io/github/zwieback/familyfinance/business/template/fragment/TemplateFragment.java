package io.github.zwieback.familyfinance.business.template.fragment;

import android.os.Bundle;

import io.github.zwieback.familyfinance.business.template.adapter.TemplateAdapter;
import io.github.zwieback.familyfinance.business.template.filter.TemplateFilter;
import io.github.zwieback.familyfinance.business.template.listener.OnTemplateClickListener;
import io.github.zwieback.familyfinance.core.fragment.EntityFragment;
import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.github.zwieback.familyfinance.databinding.ItemTemplateBinding;

import static io.github.zwieback.familyfinance.business.template.filter.TemplateFilter.TEMPLATE_FILTER;

public class TemplateFragment extends EntityFragment<TemplateView, TemplateFilter,
        ItemTemplateBinding, OnTemplateClickListener, TemplateAdapter> {

    public static TemplateFragment newInstance(TemplateFilter filter) {
        TemplateFragment fragment = new TemplateFragment();
        Bundle args = createArguments(TEMPLATE_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected TemplateAdapter createEntityAdapter() {
        TemplateFilter filter = extractFilter(TEMPLATE_FILTER);
        return new TemplateAdapter(context, clickListener, data, filter);
    }
}
