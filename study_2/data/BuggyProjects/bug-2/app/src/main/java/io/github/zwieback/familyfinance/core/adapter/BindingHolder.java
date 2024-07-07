package io.github.zwieback.familyfinance.core.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class BindingHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final B binding;

    BindingHolder(B binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
