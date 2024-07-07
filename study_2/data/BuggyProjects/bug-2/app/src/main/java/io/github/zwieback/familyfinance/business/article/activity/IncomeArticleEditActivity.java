package io.github.zwieback.familyfinance.business.article.activity;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.core.model.type.ArticleType;

public class IncomeArticleEditActivity extends ArticleEditActivity {

    @Override
    protected int getTitleStringId() {
        return R.string.income_article_activity_edit_title;
    }

    @Override
    protected ArticleType getArticleType() {
        return ArticleType.INCOME_ARTICLE;
    }
}
