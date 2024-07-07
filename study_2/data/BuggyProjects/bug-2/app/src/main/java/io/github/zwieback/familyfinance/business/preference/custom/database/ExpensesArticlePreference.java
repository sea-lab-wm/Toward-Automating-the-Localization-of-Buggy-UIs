package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.R;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.EXPENSE_ARTICLE_CODE;

public class ExpensesArticlePreference extends ArticlePreference {

    @SuppressWarnings("unused")
    public ExpensesArticlePreference(Context context, AttributeSet attrs, int defStyleAttr,
                                     int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public ExpensesArticlePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public ExpensesArticlePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public ExpensesArticlePreference(Context context) {
        super(context);
    }

    @Override
    protected int getRequestCode() {
        return EXPENSE_ARTICLE_CODE;
    }

    @Override
    protected int getSavedEntityId() {
        return databasePrefs.getExpensesArticleId();
    }

    @Override
    protected void saveEntityId(int articleId) {
        databasePrefs.setExpensesArticleId(articleId);
    }

    @Override
    protected int getPreferenceTitleRes() {
        return R.string.expenses_article_id_preference_title;
    }
}
