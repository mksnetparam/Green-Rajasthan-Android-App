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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.qrcode.QRCodeActivity;
import greenbharat.cdac.com.greenbharat.adapter.PlantsAndLand_Adapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.pojo.PlantGrowthImagePojo;
import greenbharat.cdac.com.greenbharat.pojo.PlantsAndLand_pojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

/**
 * Created by CDAC on 9/27/2016.
 */
public class PlantsAndLand extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PlantsAndLand_pojo> list = new ArrayList<PlantsAndLand_pojo>();
    private String selectedLandId = "";
    HashMap<String, PlantGrowthImagePojo> hashMapList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_plants_and_land);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Plants on Land");

        init();
        selectedLandId = getIntent().getStringExtra("land_id");
        callWebService();
    }

    private void callWebService() {
        list.clear();
        HashMap<String, String> map = new HashMap<>();
        map.put("land_id", selectedLandId);
        HomeWebService.callCommonWebService(this, map, URLHelper.GetAllottedLandTrees, NotificationCodes.GETALLOTTEDTREES);
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_with_barcode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_scan_code:
                Toast.makeText(PlantsAndLand.this, "Scan Code", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, QRCodeActivity.class);
                i.putExtra("land_id", selectedLandId);
                startActivityForResult(i, 121);
                break;

            case android.R.id.home:
                finish();
                return true;
        }
        return true;
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
                        case URLHelper.GetAllottedLandTrees:
                            String plant_image_prefix = object.getString("plant_image_prefix");
                            String baseURL = object.getString("img_prefix");

                            JSONArray imagesJsonArray = object.getJSONArray("images");
                            hashMapList.clear();
                            for (int i = 0; i < imagesJsonArray.length(); i++) {
                                JSONObject jsonObject = imagesJsonArray.getJSONObject(i);
                                PlantGrowthImagePojo plantGrowthImagePojo = (PlantGrowthImagePojo) new JSONHandler().parse(jsonObject.toString(), PlantGrowthImagePojo.class, URLHelper.BEAN_BASE_PACKAGE);
                                hashMapList.put(jsonObject.getString("plantation_id"), plantGrowthImagePojo);
                            }

                            JSONArray jsonArray = object.getJSONArray("list");
                            list.clear();

                            int length = jsonArray.length();
                            if (length > 0) {
                                for (int i = 0; i < length; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (hashMapList.containsKey(jsonObject.getString("plantation_id"))) {
                                        jsonObject.put("plant_image", plant_image_prefix + hashMapList.get(jsonObject.getString("plantation_id")).getImage());
                                        jsonObject.put("capture_date", hashMapList.get(jsonObject.getString("plantation_id")).getCapture_date());
                                        jsonObject.put("plantation_date", hashMapList.get(jsonObject.getString("plantation_id")).getPlantation_id());
                                        jsonObject.put("due_date", hashMapList.get(jsonObject.getString("plantation_id")).getDue_date());
                                        jsonObject.put("is_expired", hashMapList.get(jsonObject.getString("plantation_id")).getIs_expired());
                                    }
                                    PlantsAndLand_pojo plantsAndLand_pojo = (PlantsAndLand_pojo) new JSONHandler().parse(jsonObject.toString(), PlantsAndLand_pojo.class, URLHelper.BEAN_BASE_PACKAGE);
                                    list.add(plantsAndLand_pojo);
                                }
                                adapter = new PlantsAndLand_Adapter(PlantsAndLand.this, list, baseURL);
                                recyclerView.setAdapter(adapter);
                            }
                            else {
                                Snackbar.make(recyclerView, "No Plants are allocated to this land yet.", Snackbar.LENGTH_LONG).show();
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(NotificationCodes.GETALLOTTEDTREES));
        super.onStart();
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            callWebService();
        }
    }
}
