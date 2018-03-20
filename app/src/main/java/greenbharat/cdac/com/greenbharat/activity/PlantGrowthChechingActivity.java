package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.PlantGrowthAdapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.pojo.PlantGrowthImagePojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

public class PlantGrowthChechingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager layoutManager;
    private ArrayList<PlantGrowthImagePojo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_growth_cheching);
        ButterKnife.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Plant Growth Image Listing");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        callWebService();

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        registerForContextMenu(recyclerView);
    }

    private void callWebService() {
        list.clear();
        HashMap<String, String> valueMap = new HashMap<>();
        valueMap.put("plantation_id", getIntent().getStringExtra("plantation_id"));
        HomeWebService.callCommonWebService(this, valueMap, URLHelper.GET_PLANT_GROWTH_IMAGES, NotificationCodes.PLANT_GROWTH_CHECHKING);
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
                        case URLHelper.GET_PLANT_GROWTH_IMAGES:
                            JSONArray jsonArray = object.getJSONArray("items");
                            String base_url = object.getString("base_image_url");
                            list.clear();
                            int length = jsonArray.length();
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    PlantGrowthImagePojo plantGrowthImagePojo = (PlantGrowthImagePojo) new JSONHandler().parse(jsonObject.toString(), PlantGrowthImagePojo.class, URLHelper.BEAN_BASE_PACKAGE);
                                    list.add(plantGrowthImagePojo);
                                }
                                adapter = new PlantGrowthAdapter(PlantGrowthChechingActivity.this, list, base_url);
                                recyclerView.setAdapter(adapter);
                            }
                            else
                            {
                                Toast.makeText(context, "Plant growth is not available", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(getRegisteredSpaceReceiver, new IntentFilter(NotificationCodes.PLANT_GROWTH_CHECHKING));
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getRegisteredSpaceReceiver);
        super.onStop();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
