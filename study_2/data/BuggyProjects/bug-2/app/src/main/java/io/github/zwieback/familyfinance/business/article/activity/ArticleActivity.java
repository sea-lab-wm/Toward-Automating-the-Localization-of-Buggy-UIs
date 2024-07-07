package io.github.zwieback.familyfinance.business.article.activity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.annimon.stream.Objects;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;

import java.util.concurrent.TimeUnit;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.filter.ArticleFilter;
import io.github.zwieback.familyfinance.business.article.fragment.ArticleFragment;
import io.github.zwieback.familyfinance.business.article.lifecycle.destroyer.ArticleAsParentDestroyer;
import io.github.zwieback.familyfinance.business.article.lifecycle.destroyer.ArticleFromExpenseOperationsDestroyer;
import io.github.zwieback.familyfinance.business.article.listener.OnArticleClickListener;
import io.github.zwieback.familyfinance.core.activity.EntityFolderActivity;
import io.github.zwieback.familyfinance.core.lifecycle.destroyer.EntityDestroyer;
import io.github.zwieback.familyfinance.core.model.Article;
import io.github.zwieback.familyfinance.core.model.ArticleView;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;
import static io.github.zwieback.familyfinance.util.NumberUtils.UI_DEBOUNCE_TIMEOUT;

public abstract class ArticleActivity<
        FRAGMENT extends ArticleFragment,
        FILTER extends ArticleFilter>
        extends EntityFolderActivity<ArticleView, Article, FILTER, FRAGMENT>
        implements OnArticleClickListener {

    private MenuItem searchItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_entity_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        setupSearchItem(searchItem, menu);
        setupSearchView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupSearchItem(MenuItem searchItem, Menu menu) {
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showMenuGroupOfAddEntries(menu, false);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                showMenuGroupOfAddEntries(menu, true);
                // need invalidate options menu, because of platform bug:
                // see similar example: https://github.com/jakewharton/actionbarsherlock/issues/467
                invalidateOptionsMenu();
                return true;
            }
        });
    }

    private void setupSearchView(SearchView searchView) {
        RxSearchView.queryTextChanges(searchView)
                .debounce(UI_DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribe(searchName -> {
                    filter.setSearchName(searchName.toString());
                    applyFilter(filter);
                });
    }

    @Nullable
    abstract Integer getInitialParentId();

    @NonNull
    @Override
    protected String getResultName() {
        return RESULT_ARTICLE_ID;
    }

    @Override
    protected boolean isFirstFrame() {
        return Objects.equals(filter.getParentId(), getInitialParentId());
    }

    @Override
    protected String getFragmentTag() {
        return String.format("%s_%s", getLocalClassName(), filter.getParentId());
    }

    @Override
    public void onFolderClick(View view, ArticleView article) {
        closeSearchView();
        super.onFolderClick(view, article);
    }

    @Override
    protected void addEntity(int parentId, boolean isFolder) {
        closeSearchView();
        super.addEntity(parentId, isFolder);
    }

    @Override
    protected void editEntity(ArticleView entity) {
        closeSearchView();
        super.editEntity(entity);
    }

    @Override
    protected Class<Article> getClassOfRegularEntity() {
        return Article.class;
    }

    @Override
    protected EntityDestroyer<Article> createDestroyer(ArticleView article) {
        if (article.isFolder()) {
            return new ArticleAsParentDestroyer(this, data);
        }
        return new ArticleFromExpenseOperationsDestroyer(this, data);
    }

    private void closeSearchView() {
        searchItem.collapseActionView();
    }

    private static void showMenuGroupOfAddEntries(Menu menu, boolean shown) {
        menu.setGroupEnabled(R.id.action_add_entries, shown);
        menu.setGroupVisible(R.id.action_add_entries, shown);
    }
}
