package io.github.zwieback.familyfinance.business.article.activity;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.model.type.ArticleType;

public class ExpenseArticleEditActivity extends ArticleEditActivity {

    @Override
    protected int getTitleStringId() {
        return R.string.expense_article_activity_edit_title;
    }

    @Override
    protected ArticleType getArticleType() {
        return ArticleType.EXPENSE_ARTICLE;
    }
}
