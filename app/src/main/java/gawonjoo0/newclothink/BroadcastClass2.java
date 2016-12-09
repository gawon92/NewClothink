package gawonjoo0.newclothink;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by USER on 2016-12-07.
 */
public class BroadcastClass2 extends BroadcastReceiver {
    String INTENT_ACTION= Intent.ACTION_BOOT_COMPLETED;
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder=new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.washerbtn).setTicker("Clothink").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("Clothink").setContentText("내일은 빨래하는 날입니다! 내일의 날씨는 맑음입니다!")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationManager.notify(1,builder.build());
    }
}
