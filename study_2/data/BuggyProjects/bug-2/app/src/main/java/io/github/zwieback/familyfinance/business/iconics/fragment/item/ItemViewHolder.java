package io.github.zwieback.familyfinance.business.iconics.fragment.item;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import io.github.zwieback.familyfinance.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public final TextView name;
    public final IconicsImageView image;

    ItemViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        image = itemView.findViewById(R.id.icon);
    }
}
