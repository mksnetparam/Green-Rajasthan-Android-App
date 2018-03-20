package greenbharat.cdac.com.greenbharat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.Home;
import greenbharat.cdac.com.greenbharat.activity.LoginSignUp;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.DeviceInfoHelper.DeviceInfoHelper;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.ValidationHelper;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;

public class SignupFragment extends Fragment {
    private EditText spinnerCity,spinnerState, spinnerCountry,  input_name, input_mobile, input_email, input_password, input_confirmpassword;
    private CircleImageView image_view;
    private FancyButton button_signup;
    private HashMap<String, String> valuesMap = new HashMap<String, String>();
    private HashMap<String, String> otpMap = new HashMap<String, String>();
    private TextView loginText;
    private String enteredUserMobile = "";
    private ArrayList<String> countryNamePojoArrayList = new ArrayList<>();
    private ArrayList<String> stateNamePojoArrayList = new ArrayList<>();
    private ArrayList<String> cityNamePojoArrayList = new ArrayList<>();
    private HashMap<String, String> countryHashMap = new HashMap<>();
    private HashMap<String, String> stateHashMap = new HashMap<>();
    private HashMap<String, String> cityHashMap = new HashMap<>();
    private String selectedCityId = "", selectedStateId = "", selectedCountryId = "";

    private BroadcastReceiver mMessageReceiverCommon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.getString("result").equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case URLHelper.SIGNUP:
                            handleSignUp(jsonObject);
                            break;
                        case URLHelper.GENERATEOTP:
                            Helper.showOtpDialog(getActivity(), otpMap, URLHelper.GENERATEOTP, enteredUserMobile, NotificationCodes.signUpNotificationCode);
                            break;
                        case URLHelper.MATCHOTP:
                            callSignUpAfterOtp();
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
                        Toast.makeText(getActivity(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Something's Wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("1234", "Exception in Login services");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_registration, null);
        init(view);
        methodCall();
        HomeWebService.callCommonWebService(getActivity(), new HashMap<String, String>(), URLHelper.GETCOUNTRIES, NotificationCodes.signUpNotificationCode);
        return view;
    }

    private void init(View view) {
        input_name = (EditText) view.findViewById(R.id.input_name);
        input_email = (EditText) view.findViewById(R.id.input_email);
        input_mobile = (EditText) view.findViewById(R.id.input_mobile);
        spinnerCity = (EditText) view.findViewById(R.id.spinnerCity);
        spinnerState = (EditText) view.findViewById(R.id.spinnerState);
        spinnerCountry = (EditText) view.findViewById(R.id.spinnerCountry);
        input_password = (EditText) view.findViewById(R.id.input_password);
        input_confirmpassword = (EditText) view.findViewById(R.id.input_confirmpassword);
        button_signup = (FancyButton) view.findViewById(R.id.button_signup);
        image_view = (CircleImageView) view.findViewById(R.id.profile_pic);

        loginText = (TextView) view.findViewById(R.id.loginText);
    }

    private void methodCall() {
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginSignUp) getActivity()).changeFragment(new LoginFragment());
            }
        });
        image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ((LoginSignUp) getActivity()).selectImageOption(image_view);
            }
        });
        button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
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

    private void signUp() {
        String name = input_name.getText().toString();
        String email = input_email.getText().toString();
        String mobile = input_mobile.getText().toString();
        enteredUserMobile = mobile;
        String password = input_password.getText().toString();
        String confirm_password = input_confirmpassword.getText().toString();

        if (!ValidationHelper.validateName(name)) {
            ValidationHelper.setError(input_name);
            Helper.showSnackbar(getView(), "enter valid name");
        } else if (!ValidationHelper.validateTelephone(mobile)) {
            ValidationHelper.setError(input_mobile);
            ValidationHelper.removeError(input_name);
            Helper.showSnackbar(getView(), "enter valid mobile number");
        } else if (!ValidationHelper.validateEmail(email)) {
            ValidationHelper.removeError(input_mobile);
            ValidationHelper.setError(input_email);
            Helper.showSnackbar(getView(), "enter valid email)");
        } else if (!ValidationHelper.validatePassword(password)) {
            ValidationHelper.setError(input_password);
            ValidationHelper.removeError(input_email);
            Helper.showSnackbar(getView(), "enter valid password (min. 6 characters)");
        } else if (!ValidationHelper.validatePassword(confirm_password) || !confirm_password.equals(password)) {
            ValidationHelper.setError(input_confirmpassword);
            ValidationHelper.removeError(input_password);
            Helper.showSnackbar(getView(), "password doesn't match");
        } else if (selectedCountryId.equalsIgnoreCase("")) {
            ValidationHelper.removeError(input_confirmpassword);
            ValidationHelper.setError(spinnerCountry);
            Helper.showSnackbar(getView(), "country is required");
        } else if (selectedStateId.equalsIgnoreCase("")) {
            ValidationHelper.removeError(spinnerCountry);
            ValidationHelper.setError(spinnerState);
            Helper.showSnackbar(getView(), "state is required");
        } else if (selectedCityId.equalsIgnoreCase("")) {
            ValidationHelper.removeError(spinnerState);
            ValidationHelper.setError(spinnerCity);
            Helper.showSnackbar(getView(), "city is required");
        } else {
            ValidationHelper.removeError(spinnerCity);
            ValidationHelper.removeError(input_confirmpassword);
            valuesMap.put(SignUpWebService.user_name, name);
            valuesMap.put(SignUpWebService.password, confirm_password);
            valuesMap.put(SignUpWebService.mobile, mobile);
            valuesMap.put(SignUpWebService.email, email);
            valuesMap.put(SignUpWebService.image, "");
            valuesMap.put(SignUpWebService.socialID, "");
            valuesMap.put(SignUpWebService.country_id, selectedCountryId);
            valuesMap.put(SignUpWebService.state_id, selectedStateId);
            valuesMap.put(SignUpWebService.city_id, selectedCityId);
            String fcm_id = FirebaseInstanceId.getInstance().getToken();
            if (fcm_id == null)
                fcm_id = "";
            valuesMap.put(SignUpWebService.fcm_id, fcm_id);
            String deviceName = DeviceInfoHelper.getDeviceName();
            String androidVersion = DeviceInfoHelper.getAndroidVersion();
            String networkProvider = DeviceInfoHelper.getNetworkProvider(getActivity());
            if (deviceName == null) {
                deviceName = "";
            } else if (androidVersion == null) {
                androidVersion = "";
            } else if (networkProvider == null) {
                networkProvider = "";
            }
            valuesMap.put(SignUpWebService.device_model, deviceName);
            valuesMap.put(SignUpWebService.device_os_version, androidVersion);
            valuesMap.put(SignUpWebService.network_provider, networkProvider);

            otpMap.put(SignUpWebService.mobile, mobile);
            Log.d("1234", "signUp: "+ Arrays.asList(otpMap));
            HomeWebService.callCommonWebService(getActivity(), otpMap, URLHelper.GENERATEOTP, NotificationCodes.signUpNotificationCode);
        }
    }

    private void callSignUpAfterOtp() {
        HomeWebService.callCommonWebService(getActivity(), valuesMap, URLHelper.SIGNUP, NotificationCodes.signUpNotificationCode);
    }

    private void handleSignUp(JSONObject object) throws JSONException {
        JSONObject infoObject = object.getJSONObject("user_info");
        MyPreferenceManager preferenceManager = new MyPreferenceManager(getActivity());
        preferenceManager.storeUser(infoObject.toString());
        preferenceManager.setUserLoginStatus(true);
        Intent i = new Intent(getActivity(), Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void showSearchDialog(final String title, final EditText editText, final ArrayList<String> list) {
        final View view1 = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dailog_search_list, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(view1).create();

        ListView lv = (ListView) view1.findViewById(R.id.listViewDialog);
        EditText inputSearch = (EditText) view1.findViewById(R.id.editTextText);
        final ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.product_name, list);
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
                        HomeWebService.callCommonWebService(getActivity(), map, URLHelper.GETSTATES, NotificationCodes.signUpNotificationCode);
                        break;
                    case "Select State":
                        spinnerCity.setText("");
                        selectedStateId = stateHashMap.get(entry);
                        selectedCityId = "";
                        HashMap<String, String> map1 = new HashMap<String, String>();
                        map1.put("country_id", selectedCountryId);
                        map1.put("state_id", selectedStateId);
                        HomeWebService.callCommonWebService(getActivity(), map1, URLHelper.getCity, NotificationCodes.signUpNotificationCode);
                        break;
                    case "Select City":
                        selectedCityId = cityHashMap.get(entry);
                        break;

                }
                editText.setText(entry);
                Helper.hideKeyboard(getActivity(), view1);
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

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiverCommon);
        super.onStop();
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiverCommon, new IntentFilter(NotificationCodes.signUpNotificationCode));
        super.onStart();
    }
}
