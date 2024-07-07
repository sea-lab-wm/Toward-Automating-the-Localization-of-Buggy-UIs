package io.github.zwieback.familyfinance.business.operation.dialog;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.view.View;

import io.github.zwieback.familyfinance.business.article.activity.ArticleActivity;
import io.github.zwieback.familyfinance.business.operation.filter.OperationFilter;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ARTICLE_CODE;
import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.RESULT_ARTICLE_ID;

abstract class OperationWithArticleFilterDialog<
        F extends OperationFilter,
        B extends ViewDataBinding,
        AA extends ArticleActivity>
        extends OperationFilterDialog<F, B> {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ARTICLE_CODE:
                int articleId = extractId(resultIntent, RESULT_ARTICLE_ID);
                loadArticle(articleId);
                break;
        }
    }

    @Override
    protected void bind(F filter) {
        getArticleEdit().setOnClickListener(this::onArticleClick);
        getArticleEdit().setOnClearTextListener(this::onArticleRemoved);

        loadArticle(filter.getArticleId());

        super.bind(filter);
    }

    abstract Class<AA> getArticleActivityClass();

    @SuppressWarnings("unused")
    private void onArticleClick(View view) {
        Intent intent = new Intent(getContext(), getArticleActivityClass());
        startActivityForResult(intent, ARTICLE_CODE);
    }

    private void onArticleRemoved() {
        filter.setArticleId(null);
    }
}
