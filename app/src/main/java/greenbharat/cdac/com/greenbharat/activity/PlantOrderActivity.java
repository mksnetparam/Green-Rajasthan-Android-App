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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.OrderPlantAdapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.pojo.PlantOrderPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

public class PlantOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PlantOrderPojo> list = new ArrayList<PlantOrderPojo>();
    // local brodcast receiver to receive json response
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject object = new JSONObject(message);
                String result = object.getString("result");
                if (result.equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.GETPLANTORDERS:
                            Log.d("1234", "broadcast: ");
                            handleOrders(object);
                            break;
                    }
                } else {
                    Toast.makeText(PlantOrderActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_order);
        getSupportActionBar().setTitle("Select Plant Order");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
        callWebService();
    }

    private void callWebService() {
        list.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("user_id", new MyPreferenceManager(this).getUserPojo().getUser_id());
        Log.d("1234", "callWebService: ");
        HomeWebService.callCommonWebService(this, map, URLHelper.GETPLANTORDERS, NotificationCodes.GETPLANTORDERS);
    }

    private void handleOrders(JSONObject object) throws Exception {
        JSONArray array = object.getJSONArray("orders");
        Log.d("1234", "handleOrders: ");
        int length = array.length();
        if (length > 0)
            for (int i = 0; i < length; i++) {
                Log.d("1234", "loop: ");
                JSONObject object1 = array.getJSONObject(i);
                PlantOrderPojo pojo = (PlantOrderPojo) new JSONHandler().parse(object1.toString(), PlantOrderPojo.class, URLHelper.BEAN_BASE_PACKAGE);
                list.add(pojo);
            }
        else
            Snackbar.make(recyclerView, "No Order Found", Snackbar.LENGTH_LONG).show();
        OrderPlantAdapter adapter = new OrderPlantAdapter(this, list);
        Log.d("1234", "adapter: ");
        recyclerView.setAdapter(adapter);
    }

    public void startActivityForResult(Intent intent) {
        startActivityForResult(intent, 212);
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.GETPLANTORDERS));
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callWebService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
