package io.github.zwieback.familyfinance.business.sms.handler;

import android.app.Notification;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import org.threeten.bp.LocalDate;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.zwieback.familyfinance.business.sms.model.dto.SmsDto;
import io.github.zwieback.familyfinance.business.sms_pattern.query.SmsPatternQueryBuilder;
import io.github.zwieback.familyfinance.business.template.activity.helper.TemplateQualifier;
import io.github.zwieback.familyfinance.core.model.SmsPatternView;
import io.github.zwieback.familyfinance.core.model.TemplateView;
import io.github.zwieback.familyfinance.util.DateUtils;
import io.github.zwieback.familyfinance.util.NumberUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

import static io.github.zwieback.familyfinance.business.sms.common.SmsConst.SMS_NOTIFICATION_ID;

public class SmsHandler {

    private static final String CHANNEL_ID = "Family Finance Sms Handler Channel Id";
    private static final String CHANNEL_NAME = "Family Finance Sms Handler Channel";
    private static final int REQUEST_CODE = 4944;

    @NonNull
    private final Context context;
    @NonNull
    private final ReactiveEntityStore<Persistable> data;
    @NonNull
    private final TemplateQualifier templateQualifier;

    public SmsHandler(@NonNull Context context,
                      @NonNull ReactiveEntityStore<Persistable> data) {
        this.context = context;
        this.data = data;
        this.templateQualifier = new TemplateQualifier(context, data);
    }

    public void handleSms(@NonNull SmsDto smsDto) {
        List<SmsPatternView> smsPatterns = SmsPatternQueryBuilder.create(data)
                .setSender(smsDto.getSender())
                .orderByCommon()
                .build()
                .toList();

        Optional<SmsPatternView> smsPatternOptional = Stream.of(smsPatterns)
                .filter(smsPattern -> doesPatternMatch(smsDto, smsPattern))
                .findFirst();

        smsPatternOptional.ifPresent(smsPattern -> parseSmsAndGenerateNotification(smsDto, smsPattern));
    }

    private boolean doesPatternMatch(@NonNull SmsDto smsDto, @NonNull SmsPatternView smsPattern) {
        Pattern pattern = Pattern.compile(smsPattern.getRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(smsDto.getBody());
        return matcher.matches();
    }

    private void parseSmsAndGenerateNotification(@NonNull SmsDto smsDto, @NonNull SmsPatternView smsPattern) {
        Pattern pattern = Pattern.compile(smsPattern.getRegex(), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(smsDto.getBody());
        if (!matcher.matches()) {
            return;
        }
        String date = null;
        if (smsPattern.getDateGroup() != null) {
            date = matcher.group(smsPattern.getDateGroup());
        }
        String value = null;
        if (smsPattern.getValueGroup() != null) {
            value = matcher.group(smsPattern.getValueGroup());
        }
        LocalDate operationDate = DateUtils.sberbankDateToLocalDate(date);
        BigDecimal operationValue = NumberUtils.sberbankNumberToBigDecimal(value);
        findTemplate(smsPattern, buildAndSendNotificationOnSuccess(operationDate, operationValue));
    }

    private void findTemplate(@NonNull SmsPatternView smsPattern,
                              @NonNull Consumer<TemplateView> onSuccess) {
        data.findByKey(TemplateView.class, smsPattern.getTemplateId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);
    }

    @NonNull
    private Consumer<TemplateView> buildAndSendNotificationOnSuccess(@NonNull LocalDate operationDate,
                                                                     @Nullable BigDecimal operationValue) {
        return foundTemplate -> {
            Notification notification = buildNotification(foundTemplate, operationDate, operationValue);
            sendNotification(notification);
        };
    }

    @NonNull
    private Notification buildNotification(@NonNull TemplateView template,
                                           @NonNull LocalDate operationDate,
                                           @Nullable BigDecimal operationValue) {
        return SmsNotificationBuilder.create()
                .setContext(context)
                .setTemplateQualifier(templateQualifier)
                .setChannelId(CHANNEL_ID)
                .setRequestCode(REQUEST_CODE)
                .setTemplate(template)
                .setOperationDate(operationDate)
                .setOperationValue(operationValue)
                .build();
    }

    private void sendNotification(@NonNull Notification notification) {
        SmsNotificationSender.create()
                .setContext(context)
                .setChannelId(CHANNEL_ID)
                .setChannelName(CHANNEL_NAME)
                .setNotificationId(SMS_NOTIFICATION_ID)
                .setNotification(notification)
                .send();
    }
}
