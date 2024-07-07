package io.github.zwieback.familyfinance.business.preference.custom.database;

import android.content.Context;
import android.util.AttributeSet;

import io.github.zwieback.familyfinance.R;

import static io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity.ARTICLE_CODE;

public class TransferArticlePreference extends ArticlePreference {

    @SuppressWarnings("unused")
    public TransferArticlePreference(Context context, AttributeSet attrs, int defStyleAttr,
                                     int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressWarnings("unused")
    public TransferArticlePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    public TransferArticlePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressWarnings("unused")
    public TransferArticlePreference(Context context) {
        super(context);
    }

    @Override
    protected int getRequestCode() {
        return ARTICLE_CODE;
    }

    @Override
    protected int getSavedEntityId() {
        return databasePrefs.getTransferArticleId();
    }

    @Override
    protected void saveEntityId(int articleId) {
        databasePrefs.setTransferArticleId(articleId);
    }

    @Override
    protected int getPreferenceTitleRes() {
        return R.string.transfer_article_id_preference_title;
    }
}
