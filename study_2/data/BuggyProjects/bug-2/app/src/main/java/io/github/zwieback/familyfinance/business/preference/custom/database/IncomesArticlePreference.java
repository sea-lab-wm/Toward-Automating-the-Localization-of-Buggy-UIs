package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.R;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.INCOME_ARTICLE_CODE;

public class IncomesArticlePreference extends ArticlePreference {

    @SuppressWarnings("unused")
    public IncomesArticlePreference(Context context, AttributeSet attrs, int defStyleAttr,
                                    int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public IncomesArticlePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public IncomesArticlePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public IncomesArticlePreference(Context context) {
        super(context);
    }

    @Override
    protected int getRequestCode() {
        return INCOME_ARTICLE_CODE;
    }

    @Override
    protected int getSavedEntityId() {
        return databasePrefs.getIncomesArticleId();
    }

    @Override
    protected void saveEntityId(int articleId) {
        databasePrefs.setIncomesArticleId(articleId);
    }

    @Override
    protected int getPreferenceTitleRes() {
        return R.string.incomes_article_id_preference_title;
    }
}
