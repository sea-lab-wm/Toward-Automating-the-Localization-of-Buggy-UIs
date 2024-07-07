package io.github.zwieback.familyfinance.business.sms.handler;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;

import io.github.zwieback.familyfinance.R;
import io.github.zwieback.familyfinance.business.dashboard.activity.DashboardActivity;
import io.github.zwieback.familyfinance.business.operation.activity.helper.OperationHelper;
import io.github.zwieback.familyfinance.business.template.activity.helper.TemplateQualifier;
import io.github.zwieback.familyfinance.business.template.exception.UnsupportedTemplateTypeException;
import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.github.zwieback.familyfinance.util.StringUtils;

public class SmsNotificationBuilder {

    private static final int ADD_OPERATION_IMMEDIATELY_REQUEST_CODE = 9872;
    private static final int ADD_OPERATION_IMMEDIATELY_FLAGS =
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT;

    @NonNull
    private Context context;
    @NonNull
    private TemplateQualifier templateQualifier;
    @NonNull
    private String channelId;

    private int requestCode;
    @NonNull
    private TemplateView template;
    @NonNull
    private LocalDate operationDate;
    @Nullable
    private BigDecimal operationValue;

    public static SmsNotificationBuilder create() {
        return new SmsNotificationBuilder();
    }

    public SmsNotificationBuilder setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public SmsNotificationBuilder setTemplateQualifier(@NonNull TemplateQualifier templateQualifier) {
        this.templateQualifier = templateQualifier;
        return this;
    }

    public SmsNotificationBuilder setChannelId(@NonNull String channelId) {
        this.channelId = channelId;
        return this;
    }

    public SmsNotificationBuilder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public SmsNotificationBuilder setTemplate(@NonNull TemplateView template) {
        this.template = template;
        return this;
    }

    public SmsNotificationBuilder setOperationDate(@NonNull LocalDate operationDate) {
        this.operationDate = operationDate;
        return this;
    }

    public SmsNotificationBuilder setOperationValue(@Nullable BigDecimal operationValue) {
        this.operationValue = operationValue;
        return this;
    }

    public Notification build() {
        PendingIntent pendingIntent = buildPendingIntent(template, operationDate, operationValue);

        String type = context.getString(determineOperationTypeRes(template));
        String date = DateUtils.localDateToString(operationDate, DateUtils.SBERBANK_DATE_FORMATTER);
        String value = NumberUtils.bigDecimalToString(operationValue, StringUtils.QUESTION);
        String contentText = context.getString(R.string.sms_received_content, type, value, template.getName(), date);
        String contentTitle = context.getString(R.string.sms_received);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                // don't include the default values (sound, vibration, light),
                // because they are included in the input sms
                .setDefaults(0)
                .setAutoCancel(true);

        if (mayToAddOperationImmediately(template, operationDate, operationValue)) {
            PendingIntent addOperationImmediatelyPendingIntent =
                    buildAddOperationImmediatelyPendingIntent(template, operationDate, operationValue);

            notificationBuilder.addAction(
                    new NotificationCompat.Action.Builder(
                            0,
                            context.getString(R.string.action_add_immediately),
                            addOperationImmediatelyPendingIntent
                    )
                            .setAllowGeneratedReplies(false)
                            .setShowsUserInterface(false)
                            .build()
            );
        }

        return notificationBuilder.build();
    }

    /**
     * @see <a href="https://stackoverflow.com/a/31429210/8035065">Solution from here</a>
     */
    private PendingIntent buildPendingIntent(@NonNull TemplateView template,
                                             @NonNull LocalDate operationDate,
                                             @Nullable BigDecimal operationValue) {
        OperationHelper<?> operationHelper = templateQualifier.determineHelper(template);
        Intent notificationIntent = operationHelper.getIntentToAdd(
                template.getArticleId(), template.getAccountId(), template.getTransferAccountId(),
                template.getOwnerId(), template.getCurrencyId(), template.getExchangeRateId(),
                operationDate, operationValue, template.getDescription(), template.getUrl());

//        return PendingIntent.getActivity(context, SMS_HANDLER_REQUEST_CODE,
//                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return TaskStackBuilder.create(context)
                .addParentStack(DashboardActivity.class)
                .addNextIntent(notificationIntent)
                .getPendingIntent(requestCode, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private boolean mayToAddOperationImmediately(@NonNull TemplateView template,
                                                 @NonNull LocalDate operationDate,
                                                 @Nullable BigDecimal operationValue) {
        OperationHelper<?> operationHelper = templateQualifier.determineHelper(template);
        return operationHelper.validToAddImmediately(
                template.getArticleId(), template.getAccountId(), template.getTransferAccountId(),
                template.getOwnerId(), template.getCurrencyId(), template.getExchangeRateId(),
                operationDate, operationValue, template.getDescription(), template.getUrl()
        );
    }

    private PendingIntent buildAddOperationImmediatelyPendingIntent(@NonNull TemplateView template,
                                                                    @NonNull LocalDate operationDate,
                                                                    @Nullable BigDecimal operationValue) {
        OperationHelper<?> operationHelper = templateQualifier.determineHelper(template);
        Intent addImmediatelyIntent = operationHelper.getIntentToAddImmediately(
                template.getArticleId(), template.getAccountId(), template.getTransferAccountId(),
                template.getOwnerId(), template.getCurrencyId(), template.getExchangeRateId(),
                operationDate, operationValue, template.getDescription(), template.getUrl()
        );
        return PendingIntent.getService(
                context,
                ADD_OPERATION_IMMEDIATELY_REQUEST_CODE,
                addImmediatelyIntent,
                ADD_OPERATION_IMMEDIATELY_FLAGS
        );
    }

    @StringRes
    private int determineOperationTypeRes(@NonNull TemplateView template) {
        switch (template.getType()) {
            case EXPENSE_OPERATION:
                return R.string.expense_operation_type;
            case INCOME_OPERATION:
                return R.string.income_operation_type;
            case TRANSFER_OPERATION:
                return R.string.transfer_operation_type;
            default:
                throw new UnsupportedTemplateTypeException(template.getId(), template.getType());
        }
    }

    private SmsNotificationBuilder() {
    }
}
