package greenbharat.cdac.com.greenbharat.webservices.webservicehelper;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.app.MyApplication;
import greenbharat.cdac.com.greenbharat.pojo.ReTryPojo;


/**
 * Created by lenovo1 on 8/10/2016.
 */
public class WebServiceHelper {

    static AlertDialog errorDialog;
    static LinkedList<ReTryPojo> linkedList = new LinkedList<>();

    public static void callWebService(final Context context, final String url, final HashMap<String, String> valuesMap, final String notificationCode) {
        final ProgressDialog dialog = new ProgressDialog(context, R.style.DialogColorChange);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");

        if(!((Activity) context).isFinishing())
        {
            dialog.show();
        }
        if (errorDialog != null)
        {
            try {
                errorDialog.cancel();
            }
            catch (Exception e)
            {

            }
        }

        errorDialog = new AlertDialog.Builder(context)
                .setTitle("Network Error")
                .setMessage("Please check your internet connection.")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (ReTryPojo reTryPojo : linkedList) {
                            callWebService(reTryPojo.getContext(), reTryPojo.getUrl(), reTryPojo.getMap()
                                    , reTryPojo.getNotificationCode());
                        }
                        linkedList.clear();
                    }
                })
                .create();
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((AppCompatActivity)context).finish();
                }
                return true;
            }
        });

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    dialog.cancel();
                }
                catch (Exception e)
                {

                }
                Log.d("1234", "response= " + response);

                /*
                AlertDialog.Builder builde = new AlertDialog.Builder(context);
                builde.setMessage(response);
                builde.show();*/

                Intent intent = new Intent(notificationCode);
                // You can also include some extra data.
                intent.putExtra("message", response);
                intent.putExtra("action", notificationCode);
                intent.putExtra("url", url);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    dialog.cancel();
                }
                catch (Exception e)
                {

                }
                if(!((Activity) context).isFinishing()) {
                    errorDialog.show();
                }
                ReTryPojo reTryPojo = new ReTryPojo();
                reTryPojo.setContext(context);
                reTryPojo.setUrl(url);
                reTryPojo.setNotificationCode(notificationCode);
                reTryPojo.setMap(valuesMap);
                linkedList.add(reTryPojo);
            }
        }) {
                @Override
                protected Map<String, String> getParams() {
                    // Post params to url
                    return valuesMap;
                }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(10000),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to  queue
        MyApplication.getInstance().addToRequestQueue(request);
    }
}
