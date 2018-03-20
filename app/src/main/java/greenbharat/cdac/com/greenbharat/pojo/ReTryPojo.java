package greenbharat.cdac.com.greenbharat.pojo;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by lenovo1 on 10/13/2016.
 */
public class ReTryPojo {

    private Context context;
    private String url, notificationCode;
    private HashMap<String , String> map;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotificationCode() {
        return notificationCode;
    }

    public void setNotificationCode(String notificationCode) {
        this.notificationCode = notificationCode;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }
}
