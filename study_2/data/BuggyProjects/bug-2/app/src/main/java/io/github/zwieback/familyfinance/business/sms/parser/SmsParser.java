package io.github.zwieback.familyfinance.business.sms.parser;

import android.support.annotation.NonNull;
import android.telephony.SmsMessage;

import io.github.zwieback.familyfinance.business.sms.model.dto.SmsDto;

public class SmsParser {

    @NonNull
    public SmsDto parseSms(@NonNull Object[] pdus) {
        StringBuilder bodyBuilder = new StringBuilder();
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < messages.length; i++) {
            // returns one message, in array because multipart message due to sms max char
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            // using append, because need to add multipart from before also
            bodyBuilder.append(messages[i].getMessageBody());
        }
        // sender could also be inside the loop, but there is no need
        String sender = messages[0].getOriginatingAddress();
        String body = bodyBuilder.toString();
        return new SmsDto(sender, body);
    }
}
