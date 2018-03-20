package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.Register_space_Adapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.pojo.Register_space_pojo;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;

/**
 * Created by CDAC on 9/23/2016.
 */
public class Register_Space extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Register_space_pojo> list = new ArrayList<Register_space_pojo>();
    private MyPreferenceManager myPreferenceManager;
    private HashMap<String, String> valueMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_space);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Registered Lands");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        callWebService();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        registerForContextMenu(recyclerView);
    }

    private void callWebService() {
        list.clear();
        myPreferenceManager = new MyPreferenceManager(this);
        UserPojo userPojo = null;
        try {
            userPojo = (UserPojo) new JSONHandler().parse(myPreferenceManager.getUser(), UserPojo.class, URLHelper.BEAN_BASE_PACKAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        valueMap.put(SignUpWebService.user_id, userPojo.getUser_id());
        HomeWebService.callCommonWebService(this, valueMap, URLHelper.GETREGISTEREDLANDS, NotificationCodes.getRegisteredSpace);
    }

    public void addLand(View view) {
        startActivityForResult(new Intent(this, Register_Space_form.class), 0);
    }


    public BroadcastReceiver getRegisteredSpaceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject object = new JSONObject(message);
                String result = object.getString("result");
                if (result.equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.GETREGISTEREDLANDS:
                            JSONArray jsonArray = object.getJSONArray("list");
                            list.clear();
                            int length  = jsonArray.length();
                            if (length>0)
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Register_space_pojo register_space_pojo = (Register_space_pojo) new JSONHandler().parse(jsonObject.toString(), Register_space_pojo.class, URLHelper.BEAN_BASE_PACKAGE);
                                list.add(register_space_pojo);
                            }
                            else
                                Snackbar.make(recyclerView, "No Registered Land Found", Snackbar.LENGTH_LONG).show();
                            adapter = new Register_space_Adapter(Register_Space.this, list);
                            recyclerView.setAdapter(adapter);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(getRegisteredSpaceReceiver, new IntentFilter(NotificationCodes.getRegisteredSpace));
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getRegisteredSpaceReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0)
        {
            Log.d("1234", "onActivityResult: "+0);
            callWebService();
        }
        else
        {
            Log.d("1234", "onActivityResult: ");
        }
    }
}
