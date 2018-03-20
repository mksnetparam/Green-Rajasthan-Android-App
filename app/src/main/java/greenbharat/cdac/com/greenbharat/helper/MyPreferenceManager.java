package greenbharat.cdac.com.greenbharat.helper;

import android.content.Context;
import android.content.SharedPreferences;

import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;


public class MyPreferenceManager {

    private static final String KEY_USER_LOGGED_IN = "isLogin";
    private  final String SHARE_MESSAGE = "share_message";
    private static final String NOTIFICATION_COUNT = "notiCount";
    private String TAG = MyPreferenceManager.class.getSimpleName();
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "cdac_greenBharatj";
    // Constructor

    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void storeUser(String user) {
        editor.putString("user", user);
        setUserLoginStatus(true);
        editor.commit();
    }
    public void setUserLoginStatus(boolean value)
    {
        editor.putBoolean(KEY_USER_LOGGED_IN, value);
        if(!value)
            editor.clear();
        editor.commit();
    }
    public boolean isLoggedIn()
    {
        return pref.getBoolean(KEY_USER_LOGGED_IN, false);
    }
    public String getUser() {
        return pref.getString("user", "");
    }
    public UserPojo getUserPojo()
    {
        try {
            return ((UserPojo) new JSONHandler().parse(getUser(), UserPojo.class, URLHelper.BEAN_BASE_PACKAGE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getNotificationCount() {
        return pref.getInt(NOTIFICATION_COUNT, 0);
    }

    public void setNotificationCount(int count)
    {
        editor.putInt(NOTIFICATION_COUNT, count);
        editor.commit();
    }

    public void setShareMessage(String shareMessage)
    {
        editor.putString(SHARE_MESSAGE, shareMessage);
        editor.commit();
    }

    public String getShareMessage()
    {
        return pref.getString(SHARE_MESSAGE, null);
    }
}