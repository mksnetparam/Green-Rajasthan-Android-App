package greenbharat.cdac.com.greenbharat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.helper.BannerViewHelper;
import greenbharat.cdac.com.greenbharat.helper.DeviceInfoHelper.DeviceInfoHelper;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;

public class SelectWhatToDoActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_REQ_CODE = 10;
    private static final int RC_SIGN_IN = 007;
    @InjectView(R.id.pbGoogle)
    ProgressBar pbGoogle;
    @InjectView(R.id.pbFacebook)
    ProgressBar pbFacebook;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private HashMap<String, String> valuesMap = new HashMap<String, String>();
    private EditText editTextMobile, editTextOTP;
    private String mobile = "";
    private boolean isOTPReceived;
    private AlertDialog alertDialogMobile;
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
                        case URLHelper.GENERATEOTPFORSIGNUP:
                            isOTPReceived = true;
                            alertDialogMobile.cancel();
                            showAddMobileDialog();
                            break;
                        case URLHelper.MATCHOTP:
                            break;
                    }
                } else {
                    if (jsonObject.has("msg"))
                        Toast.makeText(SelectWhatToDoActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(SelectWhatToDoActivity.this, "Something's Wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("1234", "Exception in Login services");
            }
        }
    };

    private static void checkPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_what_to_do);
        ButterKnife.inject(this);

        init();
        initGoogleApiClient();
        initFacebookLogin();
        checkPermission(this);
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new BannerViewHelper().initBanner(this);
    }

    public void login(View view) {
        Intent i = new Intent(this, LoginSignUp.class);
        i.putExtra("which", "login");
        startActivity(i);
    }

    public void register(View view) {
        Intent i = new Intent(this, LoginSignUp.class);
        i.putExtra("which", "register");
        startActivity(i);
    }

    public void facebookLogin(View view) {
        LoginManager.getInstance().logOut();
        pbFacebook.setVisibility(View.VISIBLE);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
    }

    private void initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getUserDetails(loginResult);
                        pbFacebook.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(SelectWhatToDoActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        try {
                            String id = json_object.getString("id");
                            String name = json_object.getString("name");
                            String imageUrl = "https://graph.facebook.com/" + id + "/picture?type=large";
                            if (imageUrl == null)
                                imageUrl = "";
                            callSignUpAfterOtp(id, name, "", imageUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.type(large)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    //========================================================================================Google Sign in starts here=====================================================

    public void shareText(View view) {
        Helper.share(this);
    }

    private void initGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SelectWhatToDoActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public void googleLogin(View view) {
        googleSignOut();
        pbGoogle.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personPhotoUrl = null;
            if (acct.getPhotoUrl() != null) {
                personPhotoUrl = acct.getPhotoUrl().toString();
            }
            if (personPhotoUrl == null)
                personPhotoUrl = "";
            callSignUpAfterOtp(acct.getId(), acct.getDisplayName(), acct.getEmail(), personPhotoUrl);
        } else {
            Toast.makeText(this, "login failed", Toast.LENGTH_SHORT).show();
        }
        pbGoogle.setVisibility(View.GONE);
    }
//====================================================================Google sign in ends here========================================================================

    private void callSignUpAfterOtp(String socialId, String name, String email, String imageURL) {

        valuesMap.put(SignUpWebService.user_name, name);
        valuesMap.put(SignUpWebService.password, "");
        valuesMap.put(SignUpWebService.email, email);
        valuesMap.put(SignUpWebService.image, imageURL);
        valuesMap.put(SignUpWebService.socialID, socialId);
        valuesMap.put(SignUpWebService.mobile, "");
        String fcm_id = FirebaseInstanceId.getInstance().getToken();
        if (fcm_id == null)
            fcm_id = "";
        valuesMap.put(SignUpWebService.fcm_id, fcm_id);
        String deviceName = DeviceInfoHelper.getDeviceName();
        String androidVersion = DeviceInfoHelper.getAndroidVersion();
        String networkProvider = DeviceInfoHelper.getNetworkProvider(this);
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

        Log.d("1234", "callSignUpAfterOtp: " + Arrays.asList(valuesMap));

        HomeWebService.callCommonWebService(SelectWhatToDoActivity.this, valuesMap, URLHelper.SIGNUP, NotificationCodes.whatTODONoTICODE);
    }

    private void handleSignUp(JSONObject object) throws Exception {
        JSONObject infoObject = object.getJSONObject("user_info");
        MyPreferenceManager preferenceManager = new MyPreferenceManager(this);
        preferenceManager.storeUser(infoObject.toString());
        preferenceManager.setUserLoginStatus(true);
        Intent i = new Intent(this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    //===============================================================enter mobile  dialog strats here ===============================================================
    private void showAddMobileDialog() {
        AlertDialog.Builder mobileDialogBuilder = new AlertDialog.Builder(SelectWhatToDoActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View viewDialog = inflater.inflate(R.layout.dialog_enter_mobile, null);
        final HashMap<String, String> map = new HashMap<String, String>();
        editTextMobile = (EditText) viewDialog.findViewById(R.id.mobile_editText);
        editTextOTP = (EditText) viewDialog.findViewById(R.id.otp_editText);
        final TextView textViewResend = (TextView) viewDialog.findViewById(R.id.textViewResend);
        textViewResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeWebService.callCommonWebService(SelectWhatToDoActivity.this, map, URLHelper.GENERATEOTPFORSIGNUP, NotificationCodes.whatTODONoTICODE);
            }
        });

        if (isOTPReceived) {
            editTextMobile.setFocusable(false);
            editTextMobile.setEnabled(false);
            editTextMobile.setText(mobile);
            editTextOTP.setVisibility(View.VISIBLE);
            textViewResend.setVisibility(View.VISIBLE);
        }

        Button button_opt_dialog = (Button) viewDialog.findViewById(R.id.button_opt_dialog);
        button_opt_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editTextOTP.getVisibility() == View.GONE) {
                    String eMobile = editTextMobile.getText().toString();
                    mobile = eMobile;
                    if (eMobile.length() < 10) {
                        editTextMobile.setError("Enter valid number");
                    } else {
                        map.put("mobile", eMobile);
                        editTextMobile.setError(null);
                        HomeWebService.callCommonWebService(SelectWhatToDoActivity.this, map, URLHelper.GENERATEOTPFORSIGNUP, NotificationCodes.whatTODONoTICODE);
                    }
                } else {
                    String eOtp = editTextOTP.getText().toString();
                    String eMobile = editTextMobile.getText().toString();
                    if (eOtp.length() < 6) {
                        editTextOTP.setError("Enter valid OTP");
                    } else {
                        map.put("otp", eOtp);
                        map.put("mobile", eMobile);
                        editTextOTP.setError(null);
                        HomeWebService.callCommonWebService(SelectWhatToDoActivity.this, map, URLHelper.MATCHOTP, NotificationCodes.whatTODONoTICODE);
                    }
                }
            }
        });
        mobileDialogBuilder.setView(viewDialog);
        alertDialogMobile = mobileDialogBuilder.create();
        alertDialogMobile.show();
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(SelectWhatToDoActivity.this).unregisterReceiver(mMessageReceiverCommon);
        super.onStop();
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(SelectWhatToDoActivity.this).registerReceiver(mMessageReceiverCommon, new IntentFilter(NotificationCodes.whatTODONoTICODE));
        super.onStart();
    }
}
