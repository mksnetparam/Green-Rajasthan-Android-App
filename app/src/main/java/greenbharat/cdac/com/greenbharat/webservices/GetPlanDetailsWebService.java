package greenbharat.cdac.com.greenbharat.webservices;

import android.content.Context;

import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.webservices.webservicehelper.WebServiceHelper;

/**
 * Created by CDAC on 9/22/2016.
 */
public class GetPlanDetailsWebService {

    public static void callgetPlanDetails(Context context, HashMap<String, String> valuesMap, String notificationCode)
    {
        WebServiceHelper.callWebService(context, URLHelper.GETPLANTS, valuesMap, notificationCode);
    }








}
