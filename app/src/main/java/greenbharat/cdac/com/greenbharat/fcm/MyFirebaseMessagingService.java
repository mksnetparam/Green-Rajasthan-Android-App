package greenbharat.cdac.com.greenbharat.fcm;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import greenbharat.cdac.com.greenbharat.database.MySqliteOpenHelper;
import greenbharat.cdac.com.greenbharat.database.NotificaitonTable;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.NotificationUtils;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String flag = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            flag = remoteMessage.getData().get("flag");
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        String msg = remoteMessage.getData().get("message");
        String title = remoteMessage.getData().get("title");

        if (title != null && msg != null) {
            MyPreferenceManager myPreferenceManager = new MyPreferenceManager(this);

            if (myPreferenceManager.isLoggedIn()) {
                //imageUri will contain URL of the image to be displayed with Notification
                String imageUri = remoteMessage.getData().get("image");
                String flagType = remoteMessage.getData().get("type");

                MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(this);
                SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(NotificaitonTable.MESAAGE_TITLE, title);
                cv.put(NotificaitonTable.MESSAGE_BODY, msg);
                cv.put(NotificaitonTable.is_read, "no");
                cv.put(NotificaitonTable.added_date, Helper.getTimeStamp());
                cv.put(NotificaitonTable.USER_ID, myPreferenceManager.getUserPojo().getUser_id());
                cv.put(NotificaitonTable.IMAGE, imageUri);
                NotificaitonTable.insert(db, cv);
                db.close();
                mySqliteOpenHelper.close();

                Intent intente = new Intent(NotificationCodes.HOME_NOTI);
                intente.putExtra("url", "refresh");
                intente.putExtra("message", "{\"result\":1,\"otp\":\"otp sent successfully\"}");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intente);

                if (imageUri != null) {
                    Bitmap bitmap = NotificationUtils.getBitmapfromUrl(imageUri);
                    NotificationUtils.sendNotification(this, title, msg, bitmap, flagType);
                } else
                    NotificationUtils.sendNotification(this, msg, title, flagType);
            }
        }
    }
}