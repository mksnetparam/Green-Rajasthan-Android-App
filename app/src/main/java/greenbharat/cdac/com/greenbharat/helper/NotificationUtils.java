package greenbharat.cdac.com.greenbharat.helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.LoginSignUp;
import greenbharat.cdac.com.greenbharat.activity.PlantOrderActivity;
import greenbharat.cdac.com.greenbharat.activity.PlantsAndLand;
import greenbharat.cdac.com.greenbharat.activity.Register_Space;
import greenbharat.cdac.com.greenbharat.activity.Splash;
import greenbharat.cdac.com.greenbharat.activity.TrackProgress;
import greenbharat.cdac.com.greenbharat.app.MyApplication;

/**
 * Created by lenovo1 on 10/19/2016.
 */
public class NotificationUtils {

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public static void sendNotification(Context context, String messageBody, String title, String flagType) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, getRedirectIntent(context, flagType), PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification_app_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))/*Notification with Image*/
                .setAutoCancel(true)
                .setColor(Color.parseColor("#1fb272"))
//                .setSound(defaultSoundUri)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);
        Notification notif = notificationBuilder.build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notif.vibrate = new long[]{100, 250, 100, 500};
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
        }
        notificationManager.notify((int) (Math.random() * 100) /* ID of notification */, notificationBuilder.build());
    }

    public static void sendNotification(Context context, String title, String messageBody, Bitmap image, String flagType) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, getRedirectIntent(context, flagType), PendingIntent.FLAG_ONE_SHOT);
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(largeIcon)/*Notification icon image*/
                .setSmallIcon(R.drawable.ic_notification_app_icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image))/*Notification with Image*/
                .setAutoCancel(true)
                .setColor(Color.parseColor("#1fb272"))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

        Notification mNotification = notificationBuilder.build();
        mNotification.priority = Notification.PRIORITY_MAX;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
        }
        notificationManager.notify((int) (Math.random() * 1000)/* ID of notification */, mNotification);
    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public static Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    public static Intent getRedirectIntent(Context context, String flagType) {
        Intent intent = null;
        if (flagType == null) {
            intent = new Intent(context, greenbharat.cdac.com.greenbharat.activity.Notification.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        } else {
            try {
                JSONObject object = new JSONObject(flagType);
                String type = object.getString("type");
                switch (type) {
                    case "land":
                        String land_id = null;
                        if (object.has("land_id"))
                            land_id = object.getString("land_id");
                        if (land_id == null) {
                            intent = new Intent(context, Register_Space.class);
                        } else {
                            intent = new Intent(context, PlantsAndLand.class);
                            intent.putExtra("land_id", land_id);
                        }
                        break;
                    case "order":
                        String order_id = null;
                        if (object.has("order_id"))
                            order_id = object.getString("order_id");
                        if (order_id == null) {
                            intent = new Intent(context, PlantOrderActivity.class);
                        } else {
                            intent = new Intent(context, TrackProgress.class);
                            intent.putExtra("order_id", order_id);
                        }
                        break;
                    case "inactive":
                        intent = new Intent(context, Splash.class);
                        new MyPreferenceManager(context).setUserLoginStatus(false);
                        if (!isAppIsInBackground(context)) {
                            NotificationUtils.clearNotifications();
                            Intent i = new Intent(context, LoginSignUp.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(i);
                        }
                        break;
                    default:
                        intent = new Intent(context, greenbharat.cdac.com.greenbharat.activity.Notification.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        }
    }

}
