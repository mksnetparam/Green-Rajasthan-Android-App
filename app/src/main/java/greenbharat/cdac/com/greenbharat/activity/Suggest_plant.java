package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.Suggestplant_Adapter;
import greenbharat.cdac.com.greenbharat.customfonts.MyTextViewLight;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.otpservices.SmsListener;
import greenbharat.cdac.com.greenbharat.otpservices.SmsReceiver;
import greenbharat.cdac.com.greenbharat.pojo.Suggestplant_pojo;
import greenbharat.cdac.com.greenbharat.webservices.GetPlanDetailsWebService;


public class Suggest_plant extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Suggestplant_pojo> list = new ArrayList<Suggestplant_pojo>();
    Button button_proceed_pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suggest_plant);

        getSupportActionBar().setTitle("Suggest Plants");

        init();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callWebService();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        callWebService();

    }
    private void callWebService()
    {
        GetPlanDetailsWebService.callgetPlanDetails(this, new HashMap<String, String>(), NotificationCodes.getTreeDetails);
    }
    private void init()
    {

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        button_proceed_pay = (Button) findViewById(R.id.button_proceed_pay);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
        button_proceed_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyPreferenceManager preferenceManager = new MyPreferenceManager(Suggest_plant.this);
                if (preferenceManager.isLoggedIn()) {
                    showOtpDialog();
                } else {
                    /*Intent intent = new Intent(Suggest_plant.this, LoginSignUp.class);
                    intent.putExtra("isMiddle", true);
                    startActivity(intent);*/
                    showOtpDialog();
                }

            }
        });
    }


    public BroadcastReceiver mTreeDetailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String message = intent.getStringExtra("message");

            Log.d("1234:", "get Tree details" + message);

            try
            {
                JSONObject object = new JSONObject(message);
                String result = object.getString("result");

                if (result.equals("1"))
                {

                    JSONArray jsonArray = object.getJSONArray("tree_detail");
                    list.clear();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        Suggestplant_pojo suggestplant_pojo = (Suggestplant_pojo) new JSONHandler().parse(jsonObject.toString(), Suggestplant_pojo.class, URLHelper.BEAN_BASE_PACKAGE);
                        list.add(suggestplant_pojo);
                    }

                    adapter = new Suggestplant_Adapter(Suggest_plant.this, list);
                    recyclerView.setAdapter(adapter);

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onStart() {

        LocalBroadcastManager.getInstance(this).registerReceiver(mTreeDetailsReceiver, new IntentFilter(NotificationCodes.getTreeDetails));

        super.onStart();
    }

    @Override
    protected void onStop() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTreeDetailsReceiver);

        super.onStop();
    }
    private void showOtpDialog()
    {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialogView = layoutInflater.inflate(R.layout.otp_layout_dialog, null, false);

        final EditText editOtpText = (EditText) dialogView.findViewById(R.id.edit_otp);

        builder.setView(dialogView);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        MyTextViewLight resendTextView  = (MyTextViewLight) dialogView.findViewById(R.id.text_resent_otp);
        resendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                HashMap<String, String> otpMap = new HashMap<String, String>();

            }
        });
        FancyButton submitButton = (FancyButton) dialogView.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GradientDrawable drawable = (GradientDrawable) editOtpText.getBackground();

                if (editOtpText.getText().toString().equals("") || editOtpText.getText().toString().isEmpty()) {
                    drawable.setStroke(2, Color.RED);
                } else {
                    drawable.setStroke(2, Color.parseColor("#24d2ff"));

                }
            }
        });
        FancyButton cancelButton = (FancyButton) dialogView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                String otp = messageText.substring(messageText.indexOf(":") + 1);
                editOtpText.setText(otp);
            }
        });
    }
}




