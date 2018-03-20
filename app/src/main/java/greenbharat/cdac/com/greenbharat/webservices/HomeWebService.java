package greenbharat.cdac.com.greenbharat.webservices;

import android.content.Context;
import java.util.HashMap;
import greenbharat.cdac.com.greenbharat.webservices.webservicehelper.WebServiceHelper;

/**
 * Created by lenovo1 on 8/17/2016.
 */
public class HomeWebService {
    public static void callCommonWebService(Context context, HashMap<String, String> valuesMap, String url,  String notificationCode)
    {
        WebServiceHelper.callWebService(context, url, valuesMap, notificationCode);
    }
}
