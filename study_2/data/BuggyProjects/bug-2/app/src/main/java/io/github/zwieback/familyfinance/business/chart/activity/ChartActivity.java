package io.github.zwieback.familyfinance.business.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.annimon.stream.Stream;
import com.mikepenz.iconics.utils.IconicsMenuInflaterUtil;

import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.adapter.ChartFragmentPagerAdapter;
import io.github.zwieback.familyfinance.business.chart.display.ChartDisplay;
import io.github.zwieback.familyfinance.business.chart.fragment.ChartFragment;
import io.github.zwieback.familyfinance.business.chart.listener.ChartDisplayListener;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;
import io.github.zwieback.familyfinance.business.operation.listener.OperationFilterListener;
import io.github.zwieback.familyfinance.core.activity.DataActivityWrapper;

public class ChartActivity extends DataActivityWrapper
        implements OperationFilterListener<OperationFilter>, ChartDisplayListener {

    private static final String TAB_POSITION = "tabPosition";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChartFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTabs();
    }

    @Override
    protected void setupContentView() {
        setContentView(R.layout.activity_chart);
    }

    @Override
    protected int getTitleStringId() {
        return R.string.chart_activity_title;
    }

    private void setupTabs() {
        pagerAdapter = new ChartFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ChartFragment chartFragment = findFragmentByPosition(position);
                if (chartFragment != null && !chartFragment.isDataLoaded()) {
                    chartFragment.refreshData();
                }
            }
        });
        tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<Integer> menuIds = new ArrayList<>();
        menuIds.add(R.menu.menu_entity_filter);
        menuIds.add(R.menu.menu_chart_display);

        MenuInflater inflater = getMenuInflater();
        Stream.of(menuIds).forEach(menuId ->
                IconicsMenuInflaterUtil.inflate(inflater, this, menuId, menu));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ChartFragment chartFragment;
        switch (item.getItemId()) {
            case R.id.action_filter:
                chartFragment = findFragment();
                if (chartFragment != null) {
                    chartFragment.showFilterDialog();
                }
                return true;
            case R.id.action_display:
                chartFragment = findFragment();
                if (chartFragment != null) {
                    chartFragment.showDisplayDialog();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
    }

    // -----------------------------------------------------------------------------------------
    // Fragment methods
    // -----------------------------------------------------------------------------------------

    @Nullable
    private ChartFragment findFragment() {
        return findFragmentByPosition(tabLayout.getSelectedTabPosition());
    }

    @Nullable
    private ChartFragment findFragmentByPosition(int position) {
        return pagerAdapter.findFragment(position);
    }

    // -----------------------------------------------------------------------------------------
    // Filter methods
    // -----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public void onApplyFilter(OperationFilter filter) {
        ChartFragment chartFragment = findFragment();
        if (chartFragment != null) {
            chartFragment.onApplyFilter(filter);
        }
    }

    // -----------------------------------------------------------------------------------------
    // Display methods
    // -----------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public void onApplyDisplay(ChartDisplay display) {
        ChartFragment chartFragment = findFragment();
        if (chartFragment != null) {
            chartFragment.onApplyDisplay(display);
        }
    }
}
