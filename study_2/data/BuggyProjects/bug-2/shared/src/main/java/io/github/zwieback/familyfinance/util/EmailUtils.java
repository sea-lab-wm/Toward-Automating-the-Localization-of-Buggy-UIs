package io.github.zwieback.familyfinance.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import io.github.zwieback.familyfinance.core.R;

public final class EmailUtils {

    private static final String MAIL_TO_SCHEME = "mailto:";

    public static void sendEmail(@NonNull Context context, @NonNull String sendTo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(MAIL_TO_SCHEME + sendTo));
        String title = context.getResources().getString(R.string.send_email);
        try {
            context.startActivity(Intent.createChooser(intent, title));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.no_email_clients_installed, Toast.LENGTH_SHORT).show();
        }
    }

    private EmailUtils() {
    }
}
