package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.TrackProgress_adapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.pojo.PlantGrowthImagePojo;
import greenbharat.cdac.com.greenbharat.pojo.PlantsAndLand_pojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

public class TrackProgress extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PlantsAndLand_pojo> list = new ArrayList<>();
    private String selectedOrderId = "";
    HashMap<String, PlantGrowthImagePojo> hashMapList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_plants_and_land);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Track Progress");

        init();
        selectedOrderId = getIntent().getStringExtra("order_id");
        callWebService();
    }

    private void callWebService() {
        list.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("order_id", selectedOrderId);

        Log.d("1234", "callWebService: "+selectedOrderId);

        HomeWebService.callCommonWebService(this, map, URLHelper.GetAllottedOrderTrees, NotificationCodes.GETALLOTTEDORDERTREES);
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject object = new JSONObject(message);
                String result = object.getString("result");
                if (result.equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.GetAllottedOrderTrees:
                            String plant_image_prefix = object.getString("plant_image_prefix");
                            String baseURL = object.getString("img_prefix");

                            JSONArray imagesJsonArray = object.getJSONArray("images");
                            hashMapList.clear();
                            int imageLength = imagesJsonArray.length();
                            Log.d("1234", "onReceive: lengthj" + imageLength);
                            for (int i = 0; i < imageLength; i++) {
                                JSONObject jsonObject = imagesJsonArray.getJSONObject(i);
                                PlantGrowthImagePojo plantGrowthImagePojo = (PlantGrowthImagePojo) new JSONHandler().parse(jsonObject.toString(), PlantGrowthImagePojo.class, URLHelper.BEAN_BASE_PACKAGE);
                                hashMapList.put(jsonObject.getString("plantation_id"), plantGrowthImagePojo);
                            }

                            JSONArray jsonArray = object.getJSONArray("list");
                            list.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (hashMapList.containsKey(jsonObject.getString("plantation_id"))) {
                                    String plantaion_id = jsonObject.getString("plantation_id");
                                    jsonObject.put("plant_image", plant_image_prefix + hashMapList.get(plantaion_id).getImage());
                                    jsonObject.put("capture_date", hashMapList.get(plantaion_id).getCapture_date());
//                                    jsonObject.put("plantation_date", hashMapList.get(plantaion_id).getPlantation_id());
                                    jsonObject.put("due_date", hashMapList.get(plantaion_id).getDue_date());
                                    jsonObject.put("is_expired", hashMapList.get(plantaion_id).getIs_expired());
                                }
                                PlantsAndLand_pojo plantsAndLand_pojo = (PlantsAndLand_pojo) new JSONHandler().parse(jsonObject.toString(), PlantsAndLand_pojo.class, URLHelper.BEAN_BASE_PACKAGE);
                                list.add(plantsAndLand_pojo);
                            }
                            adapter = new TrackProgress_adapter(TrackProgress.this, list, baseURL);
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
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(NotificationCodes.GETALLOTTEDORDERTREES));
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            callWebService();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
