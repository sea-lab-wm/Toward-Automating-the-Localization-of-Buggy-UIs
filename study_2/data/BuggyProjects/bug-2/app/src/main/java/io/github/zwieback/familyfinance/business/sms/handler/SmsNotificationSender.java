package io.github.zwieback.familyfinance.business.sms.handler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.annotation.NonNull;

public class SmsNotificationSender {

    @NonNull
    private Context context;
    @NonNull
    private String channelId;
    @NonNull
    private String channelName;

    private int notificationId;
    @NonNull
    private Notification notification;

    public static SmsNotificationSender create() {
        return new SmsNotificationSender();
    }

    public SmsNotificationSender setContext(@NonNull Context context) {
        this.context = context;
        return this;
    }

    public SmsNotificationSender setChannelId(@NonNull String channelId) {
        this.channelId = channelId;
        return this;
    }

    public SmsNotificationSender setChannelName(@NonNull String channelName) {
        this.channelName = channelName;
        return this;
    }

    public SmsNotificationSender setNotificationId(int notificationId) {
        this.notificationId = notificationId;
        return this;
    }

    public SmsNotificationSender setNotification(@NonNull Notification notification) {
        this.notification = notification;
        return this;
    }

    public void send() {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            addWorkaroundForAndroidO(notificationManager);
            notificationManager.notify(notificationId, notification);
        }
    }

    /**
     * @see <a href="https://stackoverflow.com/a/47135605/8035065">Workaround from here</a>
     * and <a href="https://stackoverflow.com/a/45774186/8035065">here</a>
     */
    private void addWorkaroundForAndroidO(NotificationManager notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // don't change the importance, because a higher value makes noise
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private SmsNotificationSender() {
    }
}
