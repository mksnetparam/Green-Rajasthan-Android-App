package greenbharat.cdac.com.greenbharat.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
import greenbharat.cdac.com.greenbharat.pojo.LandAreaUnit_pojo;
import greenbharat.cdac.com.greenbharat.pojo.OrganizationPojo;
import greenbharat.cdac.com.greenbharat.pojo.RegisterSpace_details_pojo;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.RegisterSpaceWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;

public class Register_Space_form extends AppCompatActivity {

    @InjectView(R.id.pb)
    ProgressBar pb;
    @InjectView(R.id.button_cross)
    FancyButton button_cross;
    @InjectView(R.id.spinnerCountry)
    EditText spinnerCountry;
    @InjectView(R.id.spinnerState)
    EditText spinnerState;
    @InjectView(R.id.spinnerCity)
    EditText spinnerCity;
    @InjectView(R.id.spinnerOrganization)
    EditText spinnerOrganization;
    @InjectView(R.id.input_land_mobile)
    EditText input_land_mobile;
    @InjectView(R.id.input_land_name)
    EditText input_land_name;
    @InjectView(R.id.land_water_state)
    MaterialBetterSpinner land_water_state;
    private MaterialBetterSpinner soil_type_spinner, land_area_unit_spinner;
    private EditText input_soilDetails, input_land_area, input_location, input_noOfPlant, input_land_address, input_tehsil, input_dist, input_owner_address;
    private String soil_type[] = {};
    private String landArea_unit[] = {};
    private FancyButton button_signup;
    private ArrayAdapter<String> arrayAdapterSoil_name, arrayAdapterLandArea_unit;
    private CheckBox checkBox;
    private String isCheckUnderOrganization = "0";
    private ArrayList<RegisterSpace_details_pojo> registerSpace_details_pojos = new ArrayList<RegisterSpace_details_pojo>();
    private ArrayList<LandAreaUnit_pojo> landAreaUnit_pojos = new ArrayList<LandAreaUnit_pojo>();
    private String selectedLandAreaUnitId = "", selectedSoilTypeId = "";
    private int selectedLandAreaUnitPosition = -1, selectedSoilTypePosition = -1;
    private HashMap<String, String> valuesMap = new HashMap<String, String>();
    private ImageView image_view;
    private String selectedCityId = "", selectedStateId = "", selectedCountryId = "";
    private ArrayList<String> countryNamePojoArrayList = new ArrayList<>();
    private ArrayList<String> stateNamePojoArrayList = new ArrayList<>();
    private ArrayList<String> cityNamePojoArrayList = new ArrayList<>();
    private HashMap<String, String> countryHashMap = new HashMap<>();
    private HashMap<String, String> stateHashMap = new HashMap<>();
    private HashMap<String, String> cityHashMap = new HashMap<>();
    private SelectImageHelper selectImageHelper;
    private String selectedOrganization = "";
    private ArrayList<OrganizationPojo> organizationPojoArrayList = new ArrayList<>();
    private String[] organizationArray;
    private PlacePicker.IntentBuilder builder;
    // local brodcast receiver to receive json response
    private BroadcastReceiver mMessageReceiverRegisterSpace = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.getString("result").equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.REGISTERLAND:
                            Register_Space_form.this.setResult(Activity.RESULT_OK, new Intent());
                            Toast.makeText(context, "Your Land Registration Submitted", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                        case URLHelper.GETLANDPROPERTY:
                            JSONArray jsonArraySoil = jsonObject.getJSONArray("soil");
                            JSONArray jsonArrayUnit = jsonObject.getJSONArray("unit");
                            parseSoilJson(jsonArraySoil);
                            parseUnitJson(jsonArrayUnit);
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
                        case URLHelper.GETORGANIZATION:
                            handleOrganizations(jsonObject);
                            break;
                    }
                } else {
                    if (jsonObject.has("msg"))
                        Toast.makeText(Register_Space_form.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Register_Space_form.this, "Something's Wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private String seletedWaterState = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register_space_form);
        ButterKnife.inject(this);
        getSupportActionBar().setTitle("Register Your Land");

        GoogleApiClient googleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();


        UserPojo userPojo = null;
        try {
            userPojo = (UserPojo) new JSONHandler().parse(new MyPreferenceManager(this).getUser(), UserPojo.class, URLHelper.BEAN_BASE_PACKAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        valuesMap.put(SignUpWebService.user_id, userPojo.getUser_id());

        init();
        methodCall();
        callWebService();
    }

    private void init() {
        soil_type_spinner = (MaterialBetterSpinner) findViewById(R.id.soil_type);
        land_area_unit_spinner = (MaterialBetterSpinner) findViewById(R.id.land_area_unit);
        input_noOfPlant = (EditText) findViewById(R.id.input_plant_capacity_qty);
        input_land_area = (EditText) findViewById(R.id.input_land_area);
        input_land_address = (EditText) findViewById(R.id.input_land_address);
        input_tehsil = (EditText) findViewById(R.id.input_tehsil);
        input_dist = (EditText) findViewById(R.id.input_dist);
        input_location = (EditText) findViewById(R.id.input_location);
        input_soilDetails = (EditText) findViewById(R.id.input_soil_details);
        checkBox = (CheckBox) findViewById(R.id.checkbox_registerspace);
        button_signup = (FancyButton) findViewById(R.id.button_register);
        arrayAdapterSoil_name = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, soil_type);
        arrayAdapterLandArea_unit = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, landArea_unit);
        land_area_unit_spinner.setAdapter(arrayAdapterLandArea_unit);
        soil_type_spinner.setAdapter(arrayAdapterSoil_name);
        image_view = (ImageView) findViewById(R.id.land_image);

        selectImageHelper = new SelectImageHelper(this, image_view, true);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"पानी है", "पानी नहीं है"});
        land_water_state.setAdapter(arrayAdapter);
    }

    private void callWebService() {
        HomeWebService.callCommonWebService(Register_Space_form.this, new HashMap<String, String>(), URLHelper.GETCOUNTRIES, NotificationCodes.registerspace);
        HomeWebService.callCommonWebService(this, new HashMap<String, String>(), URLHelper.GETORGANIZATION, NotificationCodes.registerspace);
        HomeWebService.callCommonWebService(Register_Space_form.this, new HashMap<String, String>(), URLHelper.GETLANDPROPERTY, NotificationCodes.registerspace);
    }

    public void selectLandImage(View view) {
        selectImageHelper.selectImageOption();
    }

    private void methodCall() {

        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerSpace();

            }
        });

        land_water_state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    seletedWaterState = "Yes";
                } else
                    seletedWaterState = "No";
            }
        });

        soil_type_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSoilTypeId = registerSpace_details_pojos.get(position).getSoil_id();
                selectedSoilTypePosition = position;
            }
        });

        land_area_unit_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLandAreaUnitId = landAreaUnit_pojos.get(position).getLand_area_name();
                selectedLandAreaUnitPosition = position;

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
        spinnerOrganization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOrganization();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    spinnerOrganization.setVisibility(View.VISIBLE);
                } else
                    spinnerOrganization.setVisibility(View.GONE);
            }
        });
        button_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_cross.setVisibility(View.GONE);
                input_location.setText("");
                input_location.setTag("");
            }
        });
    }

    private void registerSpace() {

        String name = input_land_name.getText().toString();
        String mobile = input_land_mobile.getText().toString();
        String landArea = input_land_area.getText().toString();
        String landAddress = input_land_address.getText().toString();
        String noOfPlant = input_noOfPlant.getText().toString();
        String soilDetails = input_soilDetails.getText().toString();
        String tehsil = input_tehsil.getText().toString();
        String dist = input_dist.getText().toString();
        String location = input_location.getTag().toString();

        View view = findViewById(R.id.scl);

        if (name.equalsIgnoreCase("")) {
            ValidationHelper.setError(input_land_name);
            input_land_name.requestFocus();
            Helper.showSnackbar(view, "enter valid name");
        } else if (!ValidationHelper.validateTelephone(mobile)) {
            ValidationHelper.removeError(input_land_name);
            ValidationHelper.setError(input_land_mobile);
            input_land_mobile.requestFocus();
            Helper.showSnackbar(view, "enter valid mobile");
        } else if (landArea.equals("")) {
            ValidationHelper.removeError(input_land_mobile);
            ValidationHelper.setError(input_land_area);
            input_land_area.requestFocus();
            Helper.showSnackbar(view, "enter valid land area");
        } else if (seletedWaterState.equalsIgnoreCase("")) {
            Helper.showSnackbar(view, "जमीन पर पानी की स्तिथि चुने");
            ValidationHelper.removeError(input_land_area);
        }
        /*else if (noOfPlant.equals("")) {
            ValidationHelper.setError(input_noOfPlant);
            input_noOfPlant.requestFocus();
            Helper.showSnackbar(view, "enter valid number of plants");
        }
        else if (selectedSoilTypeId.equals("")) {
            soil_type_spinner.setError("required");
            soil_type_spinner.requestFocus();
            Helper.showSnackbar(view, "select soil type");
            ValidationHelper.removeError(input_noOfPlant);
        }*/
        else if (landAddress.equals("")) {
            ValidationHelper.setError(input_land_address);
            input_land_address.requestFocus();
            Helper.showSnackbar(view, "enter valid land address");
            ValidationHelper.removeError(input_noOfPlant);
        } else if (selectedCountryId.equals("")) {
            ValidationHelper.setError(spinnerCountry);
            spinnerCountry.requestFocus();
            Helper.showSnackbar(view, "select country");
            ValidationHelper.removeError(input_land_address);
        } else if (selectedStateId.equals("")) {
            ValidationHelper.setError(spinnerState);
            spinnerState.requestFocus();
            Helper.showSnackbar(view, "select state");
            ValidationHelper.removeError(spinnerCountry);
        } else if (selectedCityId.equals("")) {
            ValidationHelper.setError(spinnerCity);
            spinnerCity.requestFocus();
            Helper.showSnackbar(view, "select city");
            ValidationHelper.removeError(spinnerState);
        }
      /*  else if (dist.equals("")) {
            ValidationHelper.setError(input_dist);
            input_dist.requestFocus();
            Helper.showSnackbar(view, "enter valid district");
            ValidationHelper.removeError(spinnerCity);
        } else if (tehsil.equals("")) {
            ValidationHelper.setError(input_tehsil);
            Helper.showSnackbar(view, "enter valid land tehsil");
            ValidationHelper.removeError(input_dist);
        } */
        else if (checkBox.isChecked() && selectedOrganization.equals("")) {
            isCheckUnderOrganization = "1";
            ValidationHelper.removeError(spinnerOrganization);
            Helper.showSnackbar(view, "Select Organization");
        } else {
            isCheckUnderOrganization = "0";
            valuesMap.put(RegisterSpaceWebService.land_area, landArea);
            valuesMap.put(RegisterSpaceWebService.land_area_unit, selectedLandAreaUnitId);
            valuesMap.put(RegisterSpaceWebService.plant_capacity_qty, noOfPlant);
            valuesMap.put(RegisterSpaceWebService.land_address, landAddress);
            valuesMap.put(RegisterSpaceWebService.tehsil, tehsil);
            valuesMap.put("water_state", seletedWaterState);
            valuesMap.put("name", name);
            valuesMap.put("mobile", mobile);
            valuesMap.put(RegisterSpaceWebService.district, dist);
            valuesMap.put(RegisterSpaceWebService.soil_type, selectedSoilTypeId);
            valuesMap.put(RegisterSpaceWebService.soil_detail, soilDetails);
            valuesMap.put(RegisterSpaceWebService.country_id, selectedCountryId);
            valuesMap.put(RegisterSpaceWebService.state_id, selectedStateId);
            valuesMap.put(RegisterSpaceWebService.is_registered_under_organization, isCheckUnderOrganization);
            valuesMap.put(RegisterSpaceWebService.city_id, selectedCityId);
            valuesMap.put(RegisterSpaceWebService.location, location);
            valuesMap.put(RegisterSpaceWebService.organization_id, selectedOrganization);

            String imageData = "";
            if (selectImageHelper.isImageSelected() == true) {
                Bitmap bitmap = new BitmapHelper(this).getThumbnail(selectImageHelper.getURI_FOR_SELECTED_IMAGE());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    bitmap = selectImageHelper.rotateBitmap(bitmap, selectImageHelper.getURI_FOR_SELECTED_IMAGE().getPath());
                }
                if (bitmap != null)
                    imageData = selectImageHelper.getStringImage(bitmap);
            }
            valuesMap.put(RegisterSpaceWebService.land_image_url, imageData);
            HomeWebService.callCommonWebService(Register_Space_form.this, valuesMap, URLHelper.REGISTERLAND, NotificationCodes.registerspace);
        }
    }

    private void parseUnitJson(JSONArray jsonArrayUnit) {
        try {
            String values[] = new String[jsonArrayUnit.length()];
            for (int i = 0; i < jsonArrayUnit.length(); i++) {
                JSONObject object = jsonArrayUnit.getJSONObject(i);
                LandAreaUnit_pojo landAreaUnit_pojo = new LandAreaUnit_pojo();
                landAreaUnit_pojo.setLand_area_id(object.getString("land_area_id"));
                landAreaUnit_pojo.setLand_area_name(object.getString("land_area_name"));
                values[i] = object.getString("land_area_name");
                landAreaUnit_pojos.add(landAreaUnit_pojo);
            }
            arrayAdapterLandArea_unit = new ArrayAdapter<String>(Register_Space_form.this, android.R.layout.simple_dropdown_item_1line, values);
            land_area_unit_spinner.setAdapter(arrayAdapterLandArea_unit);
        } catch (Exception e) {
            Log.d("1234:", "parse Unit");
        }
    }

    private void parseSoilJson(JSONArray jsonArraySoil) {
        String sValues[] = new String[jsonArraySoil.length()];
        try {
            for (int i = 0; i < jsonArraySoil.length(); i++) {
                JSONObject object = jsonArraySoil.getJSONObject(i);
                RegisterSpace_details_pojo register_pojo = new RegisterSpace_details_pojo();
                register_pojo.setSoil_id(object.getString("soil_id"));
                register_pojo.setSoil_name(object.getString("soil_name"));
                sValues[i] = object.getString("soil_name");
                registerSpace_details_pojos.add(register_pojo);
            }
            arrayAdapterSoil_name = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sValues);
            soil_type_spinner.setAdapter(arrayAdapterSoil_name);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void showSearchDialog(final String title, final EditText editText, final ArrayList<String> list) {
        final View view1 = ((LayoutInflater) Register_Space_form.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dailog_search_list, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(Register_Space_form.this)
                .setTitle(title)
                .setView(view1).create();

        ListView lv = (ListView) view1.findViewById(R.id.listViewDialog);
        EditText inputSearch = (EditText) view1.findViewById(R.id.editTextText);
        final ArrayAdapter adapter = new ArrayAdapter<String>(Register_Space_form.this, R.layout.list_item, R.id.product_name, list);
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
                        HomeWebService.callCommonWebService(Register_Space_form.this, map, URLHelper.GETSTATES, NotificationCodes.registerspace);
                        break;
                    case "Select State":
                        spinnerCity.setText("");
                        selectedStateId = stateHashMap.get(entry);
                        selectedCityId = "";
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        map1.put("country_id", selectedCountryId);
                        map1.put("state_id", selectedStateId);
                        HomeWebService.callCommonWebService(Register_Space_form.this, map1, URLHelper.getCity, NotificationCodes.registerspace);
                        break;
                    case "Select City":
                        selectedCityId = cityHashMap.get(entry);
                        break;

                }
                editText.setText(entry);
                dialog.cancel();
                Helper.hideKeyboard(Register_Space_form.this, view1);
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

    private void handleOrganizations(JSONObject object) throws Exception {
        JSONObject arrayDefault = object.getJSONObject("default");
        selectedOrganization = arrayDefault.getString("id");
        spinnerOrganization.setText(arrayDefault.getString("title"));
        JSONArray array = object.getJSONArray("organizations");
        int length = array.length();
        organizationArray = new String[length];
        for (int i = 0; i < length; i++) {
            JSONObject arrayObject = array.getJSONObject(i);
            OrganizationPojo pojo = (OrganizationPojo) new JSONHandler().parse(arrayObject.toString(), OrganizationPojo.class, URLHelper.BEAN_BASE_PACKAGE);
            organizationPojoArrayList.add(pojo);
            organizationArray[i] = pojo.getOrganization_name();
        }
    }

    public void selectOrganization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Organization");
        builder.setItems(organizationArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                spinnerOrganization.setText(organizationArray[item]);
                selectedOrganization = organizationPojoArrayList.get(item).getOrganization_id();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectLocation(View view) {
        try {
            pb.setVisibility(View.VISIBLE);
            startActivityForResult(builder.build(this), 121);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "update play services first", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        pb.setVisibility(View.GONE);
        if (requestCode == 121) {
            if (resultCode == RESULT_OK) {
                button_cross.setVisibility(View.VISIBLE);
                Place selectedPlace = PlacePicker.getPlace(this, result);
                LatLng latLng = selectedPlace.getLatLng();
                input_location.setTag(latLng.toString());
                input_location.setText(selectedPlace.getAddress());
            }
        } else {
            selectImageHelper.handleResult(requestCode, resultCode, result);  // call this helper class method
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        selectImageHelper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(Register_Space_form.this).unregisterReceiver(mMessageReceiverRegisterSpace);
        super.onStop();
    }


    @Override
    public void onStart() {
        builder = new PlacePicker.IntentBuilder();
        LocalBroadcastManager.getInstance(Register_Space_form.this).registerReceiver(mMessageReceiverRegisterSpace, new IntentFilter(NotificationCodes.registerspace));
        super.onStart();
    }


}
