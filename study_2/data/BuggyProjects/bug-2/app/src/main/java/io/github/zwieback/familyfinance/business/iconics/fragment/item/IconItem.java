package io.github.zwieback.familyfinance.business.iconics.fragment.item;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import io.github.zwieback.familyfinance.R;

public class IconItem extends AbstractItem<IconItem, ItemViewHolder> {

    @NonNull
    private String icon;

    public IconItem(@NonNull String icon) {
        this.icon = icon;
    }

    @Override
    public void bindView(@NonNull ItemViewHolder holder, @NonNull List<Object> payloads) {
        super.bindView(holder, payloads);

        holder.image.setIcon(new IconicsDrawable(holder.image.getContext(), icon));
        holder.name.setText(icon);

        holder.image.getIcon().color(Color.BLACK);
        holder.image.getIcon().paddingDp(0);
        holder.image.getIcon().contourWidthDp(0);
        holder.image.getIcon().contourColor(Color.TRANSPARENT);
        holder.image.setBackgroundColor(Color.TRANSPARENT);

        // as we want to respect the bounds of the original font in the icon list
        holder.image.getIcon().respectFontBounds(true);
    }

    @Override
    public ItemViewHolder getViewHolder(@NonNull View v) {
        return new ItemViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_row_icon;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.card_iconics;
    }

    @NonNull
    public String getIcon() {
        return icon;
    }
}
