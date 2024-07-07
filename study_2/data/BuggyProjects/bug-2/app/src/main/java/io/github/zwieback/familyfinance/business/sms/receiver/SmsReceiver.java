package io.github.zwieback.familyfinance.business.sms.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;

import io.github.zwieback.familyfinance.app.FamilyFinanceApplication;
import io.github.zwieback.familyfinance.business.sms.handler.SmsHandler;
import io.github.zwieback.familyfinance.business.sms.model.dto.SmsDto;
import io.github.zwieback.familyfinance.business.sms.parser.SmsParser;
import io.github.zwieback.familyfinance.util.CollectionUtils;

public class SmsReceiver extends BroadcastReceiver {

    private final SmsParser smsParser;

    public SmsReceiver() {
        smsParser = new SmsParser();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            SmsDto smsDto = extractSmsDto(intent);
            if (smsDto == null) {
                return;
            }
            FamilyFinanceApplication application = extractApplication(context);
            if (application != null) {
                SmsHandler handler = new SmsHandler(context, application.getData());
                handler.handleSms(smsDto);
            }
        }
    }

    @Nullable
    private SmsDto extractSmsDto(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return null;
        }
        // "pdus" is key for SMS in bundle
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (CollectionUtils.isEmpty(pdus)) {
            return null;
        }
        return smsParser.parseSms(pdus);
    }

    @Nullable
    private FamilyFinanceApplication extractApplication(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext instanceof FamilyFinanceApplication) {
            return (FamilyFinanceApplication) applicationContext;
        }
        return null;
    }
}
