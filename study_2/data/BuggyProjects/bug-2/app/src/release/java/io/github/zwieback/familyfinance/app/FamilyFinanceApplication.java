package io.github.zwieback.familyfinance.app;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraDialog;
import org.acra.annotation.AcraMailSender;

import io.github.zwieback.familyfinance.BuildConfig;
import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.app.info.DeveloperInfo;
import io.github.zwieback.familyfinance.app.lifecycle.creator.DatabaseTableCreator;
import io.github.zwieback.familyfinance.core.model.Models;
import io.requery.android.sqlite.DatabaseProvider;
import io.requery.android.sqlitex.SqlitexDatabaseSource;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.APP_VERSION_NAME;
import static org.acra.ReportField.BRAND;
import static org.acra.ReportField.BUILD;
import static org.acra.ReportField.CUSTOM_DATA;
import static org.acra.ReportField.DISPLAY;
import static org.acra.ReportField.LOGCAT;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.PRODUCT;
import static org.acra.ReportField.SHARED_PREFERENCES;
import static org.acra.ReportField.STACK_TRACE;
import static org.acra.ReportField.USER_COMMENT;

@AcraCore(
        reportContent = {APP_VERSION_CODE, APP_VERSION_NAME, PHONE_MODEL, ANDROID_VERSION,
                BUILD, BRAND, PRODUCT, CUSTOM_DATA, STACK_TRACE, DISPLAY, USER_COMMENT, LOGCAT,
                SHARED_PREFERENCES},
        additionalSharedPreferences = {"database_prefs", "backup_prefs"},
        buildConfigClass = BuildConfig.class,
        stopServicesOnCrash = true,
        resReportSendSuccessToast = R.string.crash_dialog_report_send_success_toast,
        resReportSendFailureToast = R.string.crash_dialog_report_send_failure_toast
)
@AcraMailSender(
        mailTo = DeveloperInfo.EMAIL
)
@AcraDialog(
        resCommentPrompt = R.string.crash_dialog_comment_prompt,
        resText = R.string.crash_dialog_text,
        resTitle = R.string.crash_dialog_title,
        resTheme = R.style.AppTheme_Dialog
)
public class FamilyFinanceApplication extends AbstractApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        if (isNewApp()) {
            createDatabase();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    @NonNull
    @Override
    protected DatabaseProvider buildDatabaseProvider() {
        // override onUpgrade to handle migrating to a new version
        DatabaseProvider source = new SqlitexDatabaseSource(this, Models.DEFAULT, DB_VERSION);
        destroyViews(source.getConfiguration());
        createViews(source.getConfiguration());
        return source;
    }

    private void createDatabase() {
        new DatabaseTableCreator(this, getData()).createTables();
    }

    private boolean isNewApp() {
        return databaseList().length == 0;
    }
}
