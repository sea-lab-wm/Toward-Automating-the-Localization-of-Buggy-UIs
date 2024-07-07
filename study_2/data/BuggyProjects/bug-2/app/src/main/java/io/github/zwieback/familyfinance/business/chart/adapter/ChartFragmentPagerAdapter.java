package io.github.zwieback.familyfinance.business.chart.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.chart.fragment.BarChartFragment;
import io.github.zwieback.familyfinance.business.chart.fragment.ChartFragment;
import io.github.zwieback.familyfinance.business.chart.fragment.HorizontalBarChartOfExpensesFragment;
import io.github.zwieback.familyfinance.business.chart.fragment.HorizontalBarChartOfIncomesFragment;
import io.github.zwieback.familyfinance.business.chart.fragment.PieChartOfExpensesFragment;
import io.github.zwieback.familyfinance.business.chart.fragment.PieChartOfIncomesFragment;

public class ChartFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGE_COUNT = 5;
    private static final int BAR_CHART = 0;
    private static final int HORIZONTAL_BAR_CHART_OF_EXPENSES = 1;
    private static final int HORIZONTAL_BAR_CHART_OF_INCOMES = 2;
    private static final int PIE_CHART_OF_EXPENSES = 3;
    private static final int PIE_CHART_OF_INCOMES = 4;

    private SparseArray<ChartFragment> registeredFragments;
    private String tabTitles[];

    public ChartFragmentPagerAdapter(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm);
        registeredFragments = new SparseArray<>();
        initTabTitles(context);
    }

    private void initTabTitles(@NonNull Context context) {
        tabTitles = new String[PAGE_COUNT];
        tabTitles[BAR_CHART] = context.getString(R.string.bar_chart_title);
        tabTitles[HORIZONTAL_BAR_CHART_OF_EXPENSES] =
                context.getString(R.string.horizontal_bar_chart_of_expenses_title);
        tabTitles[HORIZONTAL_BAR_CHART_OF_INCOMES] =
                context.getString(R.string.horizontal_bar_chart_of_incomes_title);
        tabTitles[PIE_CHART_OF_EXPENSES] = context.getString(R.string.pie_chart_of_expenses_title);
        tabTitles[PIE_CHART_OF_INCOMES] = context.getString(R.string.pie_chart_of_incomes_title);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case BAR_CHART:
                return new BarChartFragment();
            case HORIZONTAL_BAR_CHART_OF_EXPENSES:
                return new HorizontalBarChartOfExpensesFragment();
            case HORIZONTAL_BAR_CHART_OF_INCOMES:
                return new HorizontalBarChartOfIncomesFragment();
            case PIE_CHART_OF_EXPENSES:
                return new PieChartOfExpensesFragment();
            case PIE_CHART_OF_INCOMES:
                return new PieChartOfIncomesFragment();
        }
        throw new UnsupportedOperationException("Tab #" + position + " is not supported");
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ChartFragment fragment = (ChartFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public ChartFragment findFragment(int position) {
        return registeredFragments.get(position);
    }
}
