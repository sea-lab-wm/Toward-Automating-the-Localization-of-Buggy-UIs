package io.github.zwieback.familyfinance.business.dashboard.activity.drawer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.app.info.DeveloperInfo;
import io.github.zwieback.familyfinance.business.article.activity.ExpenseArticleActivity;
import io.github.zwieback.familyfinance.business.article.activity.IncomeArticleActivity;
import io.github.zwieback.familyfinance.business.backup.activity.BackupActivity;
import io.github.zwieback.familyfinance.business.currency.activity.CurrencyActivity;
import io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity;
import io.github.zwieback.familyfinance.business.exchange_rate.activity.ExchangeRateActivity;
import io.github.zwieback.familyfinance.business.person.activity.PersonActivity;
import io.github.zwieback.familyfinance.business.preference.activity.SettingsActivity;
import io.github.zwieback.familyfinance.core.drawer.DrawerListener;
import io.github.zwieback.familyfinance.core.filter.EntityFilter;
import io.github.zwieback.familyfinance.util.EmailUtils;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.EXCHANGE_RATE_CODE;
import static io.github.zwieback.familyfinance.business.exchange_rate.filter.ExchangeRateFilter.EXCHANGE_RATE_FILTER;
import static io.github.zwieback.familyfinance.core.activity.EntityActivity.INPUT_READ_ONLY;

public class DrawerCreator implements Drawer.OnDrawerItemClickListener {

    private static final int NO_SELECTED_ITEM = -1;
    private static final int CURRENCIES_ID = 100;
    private static final int EXCHANGE_RATES_ID = 101;
    private static final int PEOPLE_ID = 102;
    private static final int EXPENSE_ARTICLES_ID = 103;
    private static final int INCOME_ARTICLES_ID = 104;
    private static final int BACKUP_ID = 200;
    private static final int SETTINGS_ID = 300;
    private static final int CONTACT_ID = 301;

    private final DashboardActivity activity;

    public DrawerCreator(DashboardActivity activity) {
        this.activity = activity;
    }

    public void createDrawer(Toolbar toolbar) {
        new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withSelectedItem(NO_SELECTED_ITEM)
                .withDrawerItems(createItems())
                .withOnDrawerItemClickListener(this)
                .withOnDrawerListener(new DrawerListener(activity))
                .build();
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        switch ((int) drawerItem.getIdentifier()) {
            case CURRENCIES_ID:
                startEditableEntityActivity(CurrencyActivity.class);
                break;
            case EXCHANGE_RATES_ID:
                startEditableEntityActivityWithFilter(ExchangeRateActivity.class,
                        EXCHANGE_RATE_FILTER, activity.getExchangeRateFilter(),
                        EXCHANGE_RATE_CODE);
                break;
            case PEOPLE_ID:
                startEditableEntityActivity(PersonActivity.class);
                break;
            case EXPENSE_ARTICLES_ID:
                startEditableEntityActivity(ExpenseArticleActivity.class);
                break;
            case INCOME_ARTICLES_ID:
                startEditableEntityActivity(IncomeArticleActivity.class);
                break;
            case BACKUP_ID:
                startActivity(BackupActivity.class);
                break;
            case SETTINGS_ID:
                startActivity(SettingsActivity.class);
                break;
            case CONTACT_ID:
                EmailUtils.sendEmail(activity, DeveloperInfo.EMAIL);
                break;
        }
        return false;
    }

    private <F extends EntityFilter> void startEditableEntityActivityWithFilter(
            Class<?> activityClass, @NonNull String filterName, F filter, int requestCode) {
        Intent intent = new Intent(activity, activityClass);
        intent.putExtra(filterName, filter);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivityForResult(intent, requestCode);
    }

    private void startEditableEntityActivity(Class<?> activityClass) {
        Intent intent = new Intent(activity, activityClass);
        intent.putExtra(INPUT_READ_ONLY, false);
        startActivity(intent);
    }

    private void startActivity(Class<?> activityClass) {
        startActivity(new Intent(activity, activityClass));
    }

    private void startActivity(Intent intent) {
        activity.startActivity(intent);
    }

    private void startActivityForResult(Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    private static List<IDrawerItem> createItems() {
        List<IDrawerItem> items = new ArrayList<>();
        items.add(new PrimaryDrawerItem()
                .withIdentifier(CURRENCIES_ID)
                .withIcon(CommunityMaterial.Icon.cmd_currency_sign)
                .withName(R.string.drawer_item_currencies)
                .withSelectable(false));
        items.add(new PrimaryDrawerItem()
                .withIdentifier(EXCHANGE_RATES_ID)
                .withIcon(CommunityMaterial.Icon.cmd_cash)
                .withName(R.string.drawer_item_exchange_rates)
                .withSelectable(false));
        items.add(new PrimaryDrawerItem()
                .withIdentifier(PEOPLE_ID)
                .withIcon(CommunityMaterial.Icon.cmd_account_multiple)
                .withName(R.string.drawer_item_people)
                .withSelectable(false));
        items.add(new PrimaryDrawerItem()
                .withIdentifier(EXPENSE_ARTICLES_ID)
                .withIcon(CommunityMaterial.Icon.cmd_trending_down)
                .withName(R.string.drawer_item_expense_articles)
                .withSelectable(false));
        items.add(new PrimaryDrawerItem()
                .withIdentifier(INCOME_ARTICLES_ID)
                .withIcon(CommunityMaterial.Icon.cmd_trending_up)
                .withName(R.string.drawer_item_income_articles)
                .withSelectable(false));
        items.add(new DividerDrawerItem());
        items.add(new SecondaryDrawerItem()
                .withIdentifier(BACKUP_ID)
                .withIcon(CommunityMaterial.Icon.cmd_sync)
                .withName(R.string.drawer_item_backup)
                .withSelectable(false));
        items.add(new DividerDrawerItem());
        items.add(new SecondaryDrawerItem()
                .withIdentifier(SETTINGS_ID)
                .withIcon(FontAwesome.Icon.faw_cog)
                .withName(R.string.drawer_item_settings)
                .withSelectable(false));
        items.add(new SecondaryDrawerItem()
                .withIdentifier(CONTACT_ID)
                .withIcon(FontAwesome.Icon.faw_bullhorn)
                .withName(R.string.drawer_item_contact)
                .withSelectable(false));
        return items;
    }
}
