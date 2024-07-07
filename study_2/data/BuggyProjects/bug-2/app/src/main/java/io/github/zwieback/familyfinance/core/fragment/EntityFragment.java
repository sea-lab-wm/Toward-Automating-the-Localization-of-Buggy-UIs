package io.github.zwieback.familyfinance.core.fragment;

import android.app.Activity;
import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.app.FamilyFinanceApplication;
import io.github.zwieback.familyfinance.core.adapter.EntityAdapter;
import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import io.github.zwieback.familyfinance.core.fragment.exception.FragmentWithoutArgumentsException;
import io.github.zwieback.familyfinance.core.listener.EntityClickListener;
import io.github.zwieback.familyfinance.core.model.IBaseEntity;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

public abstract class EntityFragment<
        ENTITY extends IBaseEntity,
        FILTER extends EntityFilter,
        BINDING extends ViewDataBinding,
        LISTENER extends EntityClickListener<ENTITY>,
        ADAPTER extends EntityAdapter<ENTITY, FILTER, BINDING, LISTENER>>
        extends Fragment {

    protected Context context;
    protected LISTENER clickListener;
    protected ReactiveEntityStore<Persistable> data;
    protected ExecutorService executor;
    protected ADAPTER adapter;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof EntityClickListener) {
            this.clickListener = (LISTENER) context;
        } else {
            throw new ClassCastException(context.toString() +
                    " must implement EntityClickListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = ((FamilyFinanceApplication) ((Activity) context).getApplication()).getData();
        executor = Executors.newSingleThreadExecutor();
        adapter = createEntityAdapter();
        adapter.setExecutor(executor);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(getRecyclerViewId());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.queryAsync();
    }

    @Override
    public void onDestroy() {
        executor.shutdown();
        adapter.close();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        this.clickListener = null;
    }

    @LayoutRes
    protected int getFragmentLayoutId() {
        return R.layout.fragment_entity;
    }

    @IdRes
    protected int getRecyclerViewId() {
        return R.id.recycler_view;
    }

    protected abstract ADAPTER createEntityAdapter();

    public final void refresh() {
        adapter.queryAsync();
    }

    public final void applyFilter(FILTER filter) {
        adapter.applyFilter(filter);
        adapter.queryAsync();
    }

    protected final FILTER extractFilter(String filterName) {
        if (getArguments() == null) {
            throw new FragmentWithoutArgumentsException(getClass());
        }
        return getArguments().getParcelable(filterName);
    }

    protected static <FILTER extends EntityFilter> Bundle createArguments(String filterName,
                                                                          FILTER filter) {
        Bundle args = new Bundle();
        args.putParcelable(filterName, filter);
        return args;
    }
}
