package io.github.zwieback.familyfinance.business.sms_pattern.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import io.github.zwieback.familyfinance.business.sms_pattern.filter.SmsPatternFilter;
import io.github.zwieback.familyfinance.business.sms_pattern.listener.OnSmsPatternClickListener;
import io.github.zwieback.familyfinance.business.sms_pattern.query.SmsPatternQueryBuilder;
import io.github.zwieback.familyfinance.core.adapter.BindingHolder;
import io.github.zwieback.familyfinance.core.adapter.EntityAdapter;
import io.github.zwieback.familyfinance.core.adapter.EntityProvider;
import io.github.zwieback.familyfinance.core.model.SmsPatternView;
import io.github.zwieback.familyfinance.databinding.ItemSmsPatternBinding;
import io.requery.Persistable;
import io.requery.query.Result;
import io.requery.reactivex.ReactiveEntityStore;

public class SmsPatternAdapter extends EntityAdapter<SmsPatternView, SmsPatternFilter,
        ItemSmsPatternBinding, OnSmsPatternClickListener> {

    public SmsPatternAdapter(Context context,
                             OnSmsPatternClickListener clickListener,
                             ReactiveEntityStore<Persistable> data,
                             SmsPatternFilter filter) {
        super(SmsPatternView.$TYPE, context, clickListener, data, filter);
    }

    @Override
    protected EntityProvider<SmsPatternView> createProvider(Context context) {
        return new SmsPatternViewProvider(context);
    }

    @Override
    protected ItemSmsPatternBinding inflate(LayoutInflater inflater) {
        return ItemSmsPatternBinding.inflate(inflater);
    }

    @Override
    protected SmsPatternView extractEntity(ItemSmsPatternBinding binding) {
        return (SmsPatternView) binding.getSmsPattern();
    }

    @Override
    public Result<SmsPatternView> performQuery() {
        return SmsPatternQueryBuilder.create(data)
                .build();
    }

    @Override
    public void onBindViewHolder(SmsPatternView smsPattern, BindingHolder<ItemSmsPatternBinding> holder,
                                 int position) {
        holder.binding.setSmsPattern(smsPattern);
        provider.setupIcon(holder.binding.icon.getIcon(), smsPattern);
    }
}
