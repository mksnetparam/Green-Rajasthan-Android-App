package greenbharat.cdac.com.greenbharat.webservices;

import android.content.Context;

import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.webservices.webservicehelper.WebServiceHelper;


public class LoginWebService {

    public static void callLoginWebService(Context context, HashMap<String, String> valuesMap, String notificationCode)
    {
        WebServiceHelper.callWebService(context, URLHelper.LOGIN, valuesMap, notificationCode);
    }

}
