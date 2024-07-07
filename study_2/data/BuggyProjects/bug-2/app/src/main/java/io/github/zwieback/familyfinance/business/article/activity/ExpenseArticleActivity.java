package io.github.zwieback.familyfinance.business.article.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.article.filter.ExpenseArticleFilter;
import io.github.zwieback.familyfinance.business.article.fragment.ExpenseArticleFragment;
import io.github.zwieback.familyfinance.core.model.ArticleView;

import static io.github.zwieback.familyfinance.business.article.activity.ArticleEditActivity.INPUT_ARTICLE_ID;
import static io.github.zwieback.familyfinance.business.article.filter.ExpenseArticleFilter.EXPENSE_ARTICLE_FILTER;
import static io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity.INPUT_IS_FOLDER;
import static io.github.zwieback.familyfinance.core.activity.EntityFolderEditActivity.INPUT_PARENT_ID;

public class ExpenseArticleActivity extends ArticleActivity<ExpenseArticleFragment,
        ExpenseArticleFilter> {

    @Nullable
    @Override
    Integer getInitialParentId() {
        return databasePrefs.getExpensesArticleId();
    }

    @Override
    protected int getTitleStringId() {
        return R.string.expense_article_activity_title;
    }

    @NonNull
    @Override
    protected String getFilterName() {
        return EXPENSE_ARTICLE_FILTER;
    }

    @NonNull
    @Override
    protected ExpenseArticleFilter createDefaultFilter() {
        ExpenseArticleFilter filter = new ExpenseArticleFilter();
        filter.setParentId(getInitialParentId());
        return filter;
    }

    @Override
    protected ExpenseArticleFragment createFragment() {
        return ExpenseArticleFragment.newInstance(filter);
    }

    @Override
    protected void addEntity(int parentId, boolean isFolder) {
        super.addEntity(parentId, isFolder);
        Intent intent = new Intent(this, ExpenseArticleEditActivity.class);
        intent.putExtra(INPUT_PARENT_ID, parentId);
        intent.putExtra(INPUT_IS_FOLDER, isFolder);
        startActivity(intent);
    }

    @Override
    protected void editEntity(ArticleView article) {
        super.editEntity(article);
        Intent intent = new Intent(this, ExpenseArticleEditActivity.class);
        intent.putExtra(INPUT_ARTICLE_ID, article.getId());
        startActivity(intent);
    }
}
