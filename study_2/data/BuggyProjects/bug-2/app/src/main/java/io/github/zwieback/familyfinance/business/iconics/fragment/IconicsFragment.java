package io.github.zwieback.familyfinance.business.iconics.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnBindViewHolderListener;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.iconics.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.iconics.fragment.item.IconItem;
import io.github.zwieback.familyfinance.business.iconics.fragment.item.ItemViewHolder;
import io.github.zwieback.familyfinance.business.iconics.fragment.item.SpaceItemDecoration;
import io.github.zwieback.familyfinance.business.iconics.listener.OnIconSelectListener;

public class IconicsFragment extends Fragment {

    private static final String FONT_NAME = "FONT_NAME";

    private OnIconSelectListener mIconSelectListener;
    private List<IconItem> mIcons = new ArrayList<>();
    private FastItemAdapter<IconItem> mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private String mSearch;
    private PopupWindow mPopup;

    public static IconicsFragment newInstance(@NonNull String fontName) {
        IconicsFragment fragment = new IconicsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FONT_NAME, fontName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnIconSelectListener) {
            this.mIconSelectListener = (OnIconSelectListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                    " must implement OnIconSelectListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_iconics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = createAdapter();
        mOnScrollListener = createOnScrollListener();

        mRecyclerView = view.findViewById(R.id.icon_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

        if (getArguments() != null) {
            String fontName = getArguments().getString(FONT_NAME);
            ITypeface foundFont = Stream.of(Iconics.getRegisteredFonts(getContext()))
                    .filter(font -> font.getFontName().equalsIgnoreCase(fontName))
                    .single();
            mIcons = Stream.ofNullable(foundFont.getIcons())
                    .map(IconItem::new)
                    .collect(Collectors.toList());
            mAdapter.set(mIcons);
        }
        onSearch(mSearch);
    }

    @Override
    public void onDetach() {
        mRecyclerView.removeOnScrollListener(mOnScrollListener);
        super.onDetach();
    }

    private RecyclerView.OnScrollListener createOnScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    closePopup();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        };
    }

    private FastItemAdapter<IconItem> createAdapter() {
        FastItemAdapter<IconItem> fastAdapter = new FastItemAdapter<>();

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            mIconSelectListener.onIconSelected(item.getIcon());
            return true;
        });

        fastAdapter.withOnLongClickListener((v, adapter, item, position) -> {
            closePopup();
            int popupIconSize = getPopupIconSize();
            IconicsDrawable icon = new IconicsDrawable(v.getContext())
                    .icon(item.getIcon())
                    .sizeDp(popupIconSize)
                    .paddingRes(R.dimen.popup_icon_padding)
                    .backgroundColorRes(R.color.popup_icon_background)
                    .roundedCornersRes(R.dimen.popup_icon_rounded_corners);

            ImageView imageView = new ImageView(v.getContext());
            imageView.setImageDrawable(icon);
            int size = Utils.convertDpToPx(v.getContext(), popupIconSize);
            mPopup = new PopupWindow(imageView, size, size);
            mPopup.showAsDropDown(v);
            return true;
        });

        fastAdapter.withOnBindViewHolderListener(new OnBindViewHolderListener() {
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int position,
                                         @NonNull List<Object> payloads) {
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                IconItem item = fastAdapter.getItem(position);
                if (item != null) {
                    // set the R.id.fastadapter_item tag of this item
                    // to the item object (can be used when retrieving the view)
                    viewHolder.itemView.setTag(R.id.fastadapter_item, item);
                    // as we overwrite the default listener
                    item.bindView(holder, payloads);
                }
            }

            @Override
            public void unBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int position) {
                IconItem item = (IconItem) viewHolder.itemView.getTag(R.id.fastadapter_item);
                if (item != null) {
                    item.unbindView((ItemViewHolder) viewHolder);
                    // remove set tag's
                    viewHolder.itemView.setTag(R.id.fastadapter_item, null);
                    viewHolder.itemView.setTag(R.id.fastadapter_item_adapter, null);
                } else {
                    Log.e("FastAdapter", "The bindView method of this item should set the `Tag` " +
                            "on its itemView");
                }
            }

            @Override
            public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder viewHolder,
                                               int position) {
                // stub
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder viewHolder,
                                                 int position) {
                // stub
            }

            @Override
            public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder viewHolder,
                                                 int position) {
                return false;
            }
        });
        return fastAdapter;
    }

    private void closePopup() {
        if (mPopup != null && mPopup.isShowing()) {
            mPopup.dismiss();
        }
    }

    private int getPopupIconSize() {
        if (getContext() == null) {
            return 144;
        }
        return (int) getContext().getResources().getDimension(R.dimen.popup_icon_size);
    }

    public void onSearch(@Nullable String searchName) {
        mSearch = searchName;
        if (mAdapter != null) {
            if (TextUtils.isEmpty(searchName)) {
                mAdapter.clear();
                mAdapter.setNewList(mIcons);
            } else {
                String searchInLowerCase = mSearch.toLowerCase();
                List<IconItem> filteredIcons = filterIcons(searchInLowerCase);
                mAdapter.setNewList(filteredIcons);
            }
        }
    }

    protected List<IconItem> filterIcons(String searchInLowerCase) {
        return Stream.of(mIcons)
                .filter(icon -> icon.getIcon().toLowerCase().contains(searchInLowerCase))
                .collect(Collectors.toList());
    }
}
