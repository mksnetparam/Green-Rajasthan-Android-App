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
import greenbharat.cdac.com.greenbharat.adapter.PlantDonorAdapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.pojo.PlantDonorPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

public class PlantDonorsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PlantDonorPojo> list = new ArrayList<>();

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
                        case URLHelper.GETPLANTDONORS:
                            Log.d("1234", "broadcast: ");
                            handleAllPlantDonors(object);
                            break;
                    }
                } else {
                    Toast.makeText(PlantDonorsActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void handleAllPlantDonors(JSONObject object) throws Exception {
        JSONArray array = object.getJSONArray("donors");
        String base_image_url = object.getString("base_image_url");
        int length = array.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                Log.d("1234", "loop: ");
                JSONObject object1 = array.getJSONObject(i);
                PlantDonorPojo pojo = (PlantDonorPojo) new JSONHandler().parse(object1.toString(), PlantDonorPojo.class, URLHelper.BEAN_BASE_PACKAGE);
                list.add(pojo);
            }
            adapter = new PlantDonorAdapter(this, list, base_image_url);
            recyclerView.setAdapter(adapter);
        } else {
            Snackbar.make(recyclerView, "No Donor Found", Snackbar.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_doners);


        getSupportActionBar().setTitle("Plant Donors");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        HomeWebService.callCommonWebService(this, new HashMap<String, String>(), URLHelper.GETPLANTDONORS, NotificationCodes.DONORPLANTS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.DONORPLANTS));
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }
}
