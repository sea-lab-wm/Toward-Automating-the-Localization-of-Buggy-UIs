package io.github.zwieback.familyfinance.core.adapter;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;

import io.github.zwieback.familyfinance.core.model.IBaseEntity;

import static io.github.zwieback.familyfinance.util.StringUtils.isTextNotEmpty;

public abstract class EntityProvider<E extends IBaseEntity> {

    protected final Context context;

    protected EntityProvider(Context context) {
        this.context = context;
    }

    @NonNull
    public abstract IIcon provideDefaultIcon(E entity);

    @ColorRes
    public abstract int provideDefaultIconColor(E entity);

    @ColorInt
    public int provideTextColor(E entity) {
        throw new UnsupportedOperationException();
    }

    public final void setupIcon(@Nullable IconicsDrawable drawable, E entity) {
        if (drawable == null) {
            return;
        }
        IIcon icon = provideIcon(entity);
        if (icon == null) {
            icon = provideDefaultIcon(entity);
        }
        drawable.icon(icon);
        drawable.colorRes(provideDefaultIconColor(entity));
    }

    /**
     * Returns the icon of the entity, if it is defined.
     *
     * @see com.mikepenz.iconics.IconicsDrawable#IconicsDrawable(Context, String)
     */
    @Nullable
    private IIcon provideIcon(E entity) {
        String iconName = entity.getIconName();
        if (isTextNotEmpty(iconName)) {
            ITypeface font = Iconics.findFont(context, iconName.substring(0, 3));
            iconName = iconName.replace("-", "_");
            return font.getIcon(iconName);
        }
        return null;
    }
}
