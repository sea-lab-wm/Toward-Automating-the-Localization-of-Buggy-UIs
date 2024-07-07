package io.github.zwieback.familyfinance.business.iconics.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;

import com.annimon.stream.IntPair;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.typeface.ITypeface;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.iconics.fragment.IconicsFragment;
import io.github.zwieback.familyfinance.business.iconics.listener.OnIconSelectListener;
import io.github.zwieback.familyfinance.core.drawer.DrawerListener;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class IconicsActivity extends AppCompatActivity implements OnIconSelectListener {

    public static final String OUTPUT_ICON_NAME = "iconName";

    private List<ITypeface> mFonts;
    private String mCurrentSearch;
    private Drawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iconics);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        mFonts = getAndSortFonts();
        List<IDrawerItem> items = buildDrawerItems(mFonts);
        int mIdentifierCmd = determineIdentifierOfCommunityMaterial(mFonts);
        mDrawer = buildDrawer(toolbar, mFonts, items, mIdentifierCmd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entity_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        setupSearchView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchView(SearchView searchView) {
        RxSearchView.queryTextChanges(searchView)
                .debounce(NumberUtils.UI_DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS,
                        AndroidSchedulers.mainThread())
                .subscribe(searchName -> {
                    mCurrentSearch = searchName.toString();
                    if (mDrawer != null) {
                        Stream.of(mFonts)
                                .forEachIndexed((index, font) -> {
                                    long foundCount = findMatchedIconCount(mCurrentSearch, font);
                                    mDrawer.updateBadge(index, new StringHolder(foundCount + ""));
                                });
                    }
                    searchInFragment(mCurrentSearch);
                });
    }

    @Override
    public void onIconSelected(@NonNull String iconName) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(OUTPUT_ICON_NAME, iconName);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private long findMatchedIconCount(String searchName, ITypeface font) {
        String searchInLowerCase = searchName.toLowerCase();
        return Stream.ofNullable(font.getIcons())
                .filter(icon -> icon.toLowerCase().contains(searchInLowerCase))
                .count();
    }

    private void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private List<ITypeface> getAndSortFonts() {
        List<ITypeface> fonts = new ArrayList<>(Iconics.getRegisteredFonts(this));
        Collections.sort(fonts, (typeface1, typeface2) ->
                typeface1.getFontName().compareTo(typeface2.getFontName()));
        return fonts;
    }

    private List<IDrawerItem> buildDrawerItems(List<ITypeface> fonts) {
        List<IDrawerItem> items = new ArrayList<>(fonts.size());
        int count = 0;
        for (ITypeface font : fonts) {
            String description =
                    TextUtils.isEmpty(font.getAuthor())
                            ? font.getVersion()
                            : font.getVersion() + " - " + font.getAuthor();

            IDrawerItem drawerItem = new PrimaryDrawerItem()
                    .withName(font.getFontName())
                    .withBadge(String.valueOf(font.getIcons().size()))
                    .withDescription(description)
                    .withBadgeStyle(new BadgeStyle().withColorRes(R.color.md_grey_200))
                    .withIcon(extractRandomIcon(font))
                    .withIdentifier(count);

            items.add(drawerItem);
            count++;
        }
        return items;
    }

    private IIcon extractRandomIcon(ITypeface typeface) {
        int random = new Random().nextInt(typeface.getIcons().size());
        Optional<String> randomIconName = Stream.of(typeface.getIcons())
                .findIndexed((index, icon) -> index == random)
                .map(IntPair::getSecond);
        return typeface.getIcon(randomIconName.orElse(typeface.getIcons().iterator().next()));
    }

    private int determineIdentifierOfCommunityMaterial(List<ITypeface> fonts) {
        CommunityMaterial communityMaterial = new CommunityMaterial();
        String communityMaterialFontName = communityMaterial.getFontName();

        Optional<Integer> communityMaterialIdentifier = Stream.of(fonts)
                .findIndexed((index, font) -> communityMaterialFontName.equals(font.getFontName()))
                .map(IntPair::getFirst);
        return communityMaterialIdentifier.orElse(0);
    }

    private Drawer buildDrawer(Toolbar toolbar,
                               List<ITypeface> fonts,
                               List<IDrawerItem> items,
                               int selectedItem) {
        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDrawerItems(items)
                .withOnDrawerItemClickListener((view, position, iDrawerItem) -> {
                    loadIcons(fonts.get(position).getFontName());
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(fonts.get(position).getFontName());
                    }
                    return false;
                })
                .withOnDrawerListener(new DrawerListener(this))
                .withFireOnInitialOnClick(true)
                .withSelectedItem(selectedItem)
                .build();
    }

    private void loadIcons(@NonNull String fontName) {
        IconicsFragment iconicsFragment = IconicsFragment.newInstance(fontName);
        iconicsFragment.onSearch(mCurrentSearch);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.iconics_fragment, iconicsFragment)
                .commit();
    }

    private void searchInFragment(String searchName) {
        IconicsFragment fragment = findFragment();
        if (fragment != null) {
            fragment.onSearch(searchName);
        }
    }

    private IconicsFragment findFragment() {
        return (IconicsFragment) getSupportFragmentManager().findFragmentById(R.id.iconics_fragment);
    }
}
