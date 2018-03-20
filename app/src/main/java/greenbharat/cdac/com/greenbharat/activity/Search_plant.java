package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.SearchPlant_Adapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.pojo.SearchPlant_pojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

public class Search_plant extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SearchPlant_pojo> list = new ArrayList<SearchPlant_pojo>();
    public BroadcastReceiver mTreeDetailsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject object = new JSONObject(message);
                String result = object.getString("result");
                if (result.equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.GETPLANTS:
                            handleGetPlants(object);
                            break;
                    }
                } else {
                    Toast.makeText(Search_plant.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private CoordinatorLayout cl;
    private String selectedType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_plant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Plants");

        init();
        callWebService("", "");
    }

    private void init() {
        cl = (CoordinatorLayout) findViewById(R.id.cl);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        registerForContextMenu(recyclerView);
/*
        final EditText editText = (EditText) findViewById(R.id.input_search);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    callWebService(selectedType, editText.getText().toString());
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(cl.getWindowToken(), 0);
                    } catch (Exception e) {
                    }
                    return true;
                }
                return false;
            }
        });*/

    }

    private void callWebService(String type, String name) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tree_type", type);
        map.put("plant_name", name);
        HomeWebService.callCommonWebService(this, map, URLHelper.GETPLANTS, NotificationCodes.getTreeDetails);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void handleGetPlants(JSONObject object) throws Exception {
        JSONArray jsonArray = object.getJSONArray("plants");
        list.clear();
        int length = jsonArray.length();
        if (length > 0)
            for (int i = 0; i < length; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SearchPlant_pojo searchPlant_pojo = (SearchPlant_pojo) new JSONHandler().parse(jsonObject.toString(), SearchPlant_pojo.class, URLHelper.BEAN_BASE_PACKAGE);
                list.add(searchPlant_pojo);
            }
        else
            Snackbar.make(recyclerView, "No Plants Found", Snackbar.LENGTH_LONG).show();
        adapter = new SearchPlant_Adapter(Search_plant.this, list);
        recyclerView.setAdapter(adapter);
    }

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
}
