package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.BitmapHelper;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.SelectImageHelper;
import greenbharat.cdac.com.greenbharat.helper.ValidationHelper;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;

public class Edit_profile extends AppCompatActivity {

    @InjectView(R.id.spinnerCountry)
    EditText spinnerCountry;
    @InjectView(R.id.spinnerState)
    EditText spinnerState;
    @InjectView(R.id.spinnerCity)
    EditText spinnerCity;
    private EditText input_name, input_email, input_address;
    private CircleImageView profile_pic_edit;
    private FancyButton button_signup;
    private HashMap<String, String> valuesMap = new HashMap<String, String>();
    private MyPreferenceManager myPreferenceManager;
    private UserPojo userPojo;
    private ArrayList<String> countryNamePojoArrayList = new ArrayList<>();
    private ArrayList<String> stateNamePojoArrayList = new ArrayList<>();
    private ArrayList<String> cityNamePojoArrayList = new ArrayList<>();
    private HashMap<String, String> countryHashMap = new HashMap<>();
    private HashMap<String, String> stateHashMap = new HashMap<>();
    private HashMap<String, String> cityHashMap = new HashMap<>();
    private String selectedCityId = "", selectedStateId = "", selectedCountryId = "";
    private SelectImageHelper imageHelper;
    private BroadcastReceiver mMessageReceiverCommon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.getString("result").equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.UPDATEPROFILE:
                            Toast.makeText(Edit_profile.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                            handleUpdateProfile(jsonObject);
                            break;
                        case URLHelper.GETUSERBYID:
                            JSONObject infoObject = jsonObject.getJSONObject("user_info");
                            UserPojo userPojo = (UserPojo) new JSONHandler().parse(infoObject.toString(), UserPojo.class, URLHelper.BEAN_BASE_PACKAGE);
                            setAlreadyData(userPojo);
                            break;
                        case URLHelper.GETCOUNTRIES:
                            countryNamePojoArrayList.clear();
                            stateNamePojoArrayList.clear();
                            cityNamePojoArrayList.clear();
                            final JSONArray jsonArrayCountry = jsonObject.getJSONArray("countries");
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        parseGeoJson(jsonArrayCountry, "country_name", "country_id", countryNamePojoArrayList, countryHashMap);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case URLHelper.GETSTATES:
                            stateNamePojoArrayList.clear();
                            cityNamePojoArrayList.clear();
                            final JSONArray jsonArrayStates = jsonObject.getJSONArray("states");
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        parseGeoJson(jsonArrayStates, "state_name", "state_id", stateNamePojoArrayList, stateHashMap);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        case URLHelper.getCity:
                            cityNamePojoArrayList.clear();
                            final JSONArray jsonArrayCity = jsonObject.getJSONArray("cities");
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        parseGeoJson(jsonArrayCity, "city_name", "city_id", cityNamePojoArrayList, cityHashMap);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                    }
                } else {
                    if (jsonObject.has("msg"))
                        Toast.makeText(Edit_profile.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Edit_profile.this, "Something's Wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("1234", "Exception in Login services");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_profile);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        supportPostponeEnterTransition();
        myPreferenceManager = new MyPreferenceManager(this);
        try {
            userPojo = (UserPojo) new JSONHandler().parse(myPreferenceManager.getUser(), UserPojo.class, URLHelper.BEAN_BASE_PACKAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
        methodCall();

        HashMap<String, String> map = new HashMap<>();
        map.put(SignUpWebService.user_id, userPojo.getUser_id());
        HomeWebService.callCommonWebService(this, map, URLHelper.GETUSERBYID, NotificationCodes.editProfile);
    }

    private void setAlreadyData(UserPojo userPojo) {
        /*ImageRequest request = new ImageRequest(userPojo.getImage(), new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profile_pic_edit.setImageBitmap(response);
            }
        }, 500, 500, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyApplication.getInstance().addToRequestQueue(request);*/


        Glide.with(this)
                .load(userPojo.getImage())
                .error(R.drawable.profile)
                .fitCenter()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }
                })
                .into(profile_pic_edit);

//        Helper.downloadImageNoCache(this, userPojo.getImage(), profile_pic_edit);

        HomeWebService.callCommonWebService(this, new HashMap<String, String>(), URLHelper.GETCOUNTRIES, NotificationCodes.editProfile);
        countryNamePojoArrayList.clear();
        stateNamePojoArrayList.clear();
        cityNamePojoArrayList.clear();

        input_name.setText(userPojo.getName());
        input_name.setSelection(userPojo.getName().length());
        input_email.setText(userPojo.getEmail());
        input_address.setText(userPojo.getAddress());
        selectedCityId = userPojo.getCity_id();
        selectedCountryId = userPojo.getCountry_id();
        selectedStateId = userPojo.getState_id();

        if (!userPojo.getState_name().equals(""))
            spinnerState.setText(userPojo.getState_name());
        if (!userPojo.getCity_name().equals("")) {
            spinnerCity.setText(userPojo.getCity_name());
            HashMap<String, String> map1 = new HashMap<String, String>();
            map1.put("country_id", selectedCountryId);
            map1.put("state_id", selectedStateId);
            HomeWebService.callCommonWebService(this, map1, URLHelper.getCity, NotificationCodes.editProfile);
        }
        if (!userPojo.getCountry_name().equals("")) {
            spinnerCountry.setText(userPojo.getCountry_name());
            HashMap<String, String> map2 = new HashMap<String, String>();
            map2.put("country_id", selectedCountryId);
            HomeWebService.callCommonWebService(this, map2, URLHelper.GETSTATES, NotificationCodes.editProfile);
        }
    }

    private void init() {
        input_name = (EditText) findViewById(R.id.input_name);
        input_name.setText(userPojo.getName());

        input_email = (EditText) findViewById(R.id.input_email);
        input_email.setText(userPojo.getEmail());

        input_address = (EditText) findViewById(R.id.input_address);
        input_address.setText(userPojo.getAddress());

        button_signup = (FancyButton) findViewById(R.id.button_signup);
        profile_pic_edit = (CircleImageView) findViewById(R.id.profile_pic_edit);

        Helper.downloadImageFromDrawable(this, R.drawable.profile, profile_pic_edit);

        imageHelper = new SelectImageHelper(this, profile_pic_edit, false);
    }

    public void selectImage(View view) {
        imageHelper.selectImageOption();
    }

    private void methodCall() {
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        spinnerCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog("Select Country", spinnerCountry, countryNamePojoArrayList);
            }
        });
        spinnerState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog("Select State", spinnerState, stateNamePojoArrayList);
            }
        });
        spinnerCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog("Select City", spinnerCity, cityNamePojoArrayList);
            }
        });
    }

    private void update() {
        String name = input_name.getText().toString().trim();
        String email = input_email.getText().toString().trim();
        String address = input_address.getText().toString().trim();

        input_name.setText(name);
        input_address.setText(address);
        input_email.setText(email);

        boolean isError = false;
        if (!ValidationHelper.validateName(name)) {
            isError = true;
            ValidationHelper.setError(input_name);
        } else {
            ValidationHelper.removeError(input_name);
        }
        if (ValidationHelper.validateEmail(email)) {
            ValidationHelper.removeError(input_email);
        } else {
            isError = true;
            ValidationHelper.setError(input_email);
        }
        if (address.equals("")) {
            isError = true;
            ValidationHelper.setError(input_address);
        } else {
            ValidationHelper.removeError(input_address);
        }

        if (!isError) {
            valuesMap.put(SignUpWebService.user_name, name);
            valuesMap.put(SignUpWebService.email, email);
            valuesMap.put(SignUpWebService.address, address);
            valuesMap.put(SignUpWebService.user_id, userPojo.getUser_id());
            String imageData = "";
            if (imageHelper.isImageSelected() == true) {
                Bitmap bitmap = new BitmapHelper(this).getThumbnail(imageHelper.getURI_FOR_SELECTED_IMAGE());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    bitmap = imageHelper.rotateBitmap(bitmap, imageHelper.getURI_FOR_SELECTED_IMAGE().getPath());
                }
                if (bitmap != null)
                    imageData = imageHelper.getStringImage(bitmap);
            }

            Log.d("1234", "update: " + imageData);

            valuesMap.put(SignUpWebService.image, imageData);
            valuesMap.put(SignUpWebService.country_id, selectedCountryId);
            valuesMap.put(SignUpWebService.state_id, selectedStateId);
            valuesMap.put(SignUpWebService.city_id, selectedCityId);

            String fcm_id = FirebaseInstanceId.getInstance().getToken();
            if (fcm_id == null)
                fcm_id = "";

            Log.d("1234", "update: " + fcm_id);

            valuesMap.put(SignUpWebService.fcm_id, fcm_id);

            HomeWebService.callCommonWebService(Edit_profile.this, valuesMap, URLHelper.UPDATEPROFILE, NotificationCodes.editProfile);
        }
    }

    private void parseGeoJson(JSONArray jsonArray, String nameKey, String idKey, ArrayList<String> list,
                              HashMap<String, String> map) throws JSONException {
        int lenght = jsonArray.length();
        list.clear();
        map.clear();
        for (int i = 0; i < lenght; i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString(nameKey);
            String id = object.getString(idKey);
            list.add(name);
            map.put(name, id);
        }
    }

    private void handleUpdateProfile(JSONObject jsonObject) throws Exception {
        JSONObject infoObject = jsonObject.getJSONObject("user_info");
        MyPreferenceManager preferenceManager = new MyPreferenceManager(this);
        preferenceManager.storeUser(infoObject.toString());
        preferenceManager.setUserLoginStatus(true);
        Intent i = new Intent(this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void showSearchDialog(final String title, final EditText editText, final ArrayList<String> list) {
        final View view1 = ((LayoutInflater) Edit_profile.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dailog_search_list, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Edit_profile.this)
                .setTitle(title)
                .setView(view1).create();

        ListView lv = (ListView) view1.findViewById(R.id.listViewDialog);
        EditText inputSearch = (EditText) view1.findViewById(R.id.editTextText);
        final ArrayAdapter adapter = new ArrayAdapter<String>(Edit_profile.this, R.layout.list_item, R.id.product_name, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String entry = (String) parent.getAdapter().getItem(position);
                switch (title) {
                    case "Select Country":
                        Log.d("1234", "select country");
                        spinnerCity.setText("");
                        spinnerState.setText("");
                        selectedCountryId = countryHashMap.get(entry);
                        selectedStateId = "";
                        selectedCityId = "";
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("country_id", selectedCountryId);
                        HomeWebService.callCommonWebService(Edit_profile.this, map, URLHelper.GETSTATES, NotificationCodes.editProfile);
                        break;
                    case "Select State":
                        spinnerCity.setText("");
                        selectedStateId = stateHashMap.get(entry);
                        selectedCityId = "";
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        map1.put("country_id", selectedCountryId);
                        map1.put("state_id", selectedStateId);
                        HomeWebService.callCommonWebService(Edit_profile.this, map1, URLHelper.getCity, NotificationCodes.editProfile);
                        break;
                    case "Select City":
                        selectedCityId = cityHashMap.get(entry);
                        break;

                }
                editText.setText(entry);
                Helper.hideKeyboard(Edit_profile.this, view1);
                dialog.cancel();
            }
        });
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        imageHelper.handleResult(requestCode, resultCode, result);  // call this helper class method
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        imageHelper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
    }


    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverCommon);
        super.onStop();
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverCommon, new IntentFilter(NotificationCodes.editProfile));
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("1234", "onOptionsItemSelected: ");
        finish();
        return super.onOptionsItemSelected(item);
    }
}
