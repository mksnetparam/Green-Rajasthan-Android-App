package greenbharat.cdac.com.greenbharat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.TreeFriendAdapter;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.pojo.TreeFriendPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

/**
 * Created by CDAC on 8/9/2017.
 */

public class TreeFriendFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TreeFriendPojo> list = new ArrayList<TreeFriendPojo>();

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
                        case URLHelper.GETALLTREEFRIENDS:
                            Log.d("1234", "broadcast: ");
                            handleAllTreeFriend(object);
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree_friend, null);
        init(view);

        return view;
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        HomeWebService.callCommonWebService(getActivity(), new HashMap<String, String>(), URLHelper.GETALLTREEFRIENDS, NotificationCodes.NATURELOVER);
    }


    private void handleAllTreeFriend(JSONObject object) throws Exception {
        JSONArray array = object.getJSONArray("plant_friends");
        String base_image_url = object.getString("base_image_url");
        int length = array.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                Log.d("1234", "loop: ");
                JSONObject object1 = array.getJSONObject(i);
                TreeFriendPojo pojo = (TreeFriendPojo) new JSONHandler().parse(object1.toString(), TreeFriendPojo.class, URLHelper.BEAN_BASE_PACKAGE);
                list.add(pojo);
            }
            adapter = new TreeFriendAdapter(getActivity(), list, base_image_url);
            recyclerView.setAdapter(adapter);
        } else {
            Snackbar.make(recyclerView, "No Order Found", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.NATURELOVER));
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

}