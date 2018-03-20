package greenbharat.cdac.com.greenbharat.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.Home;
import greenbharat.cdac.com.greenbharat.activity.LoginSignUp;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.ValidationHelper;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;


/**
 * Created by CDAC on 8/29/2016.
 */
public class LoginFragment extends Fragment {
    EditText input_mobile, input_password;
    TextView forgot_Password, registerText;
    FancyButton button_login;
    HashMap<String, String> otpMap = new HashMap<String, String>();
    private AlertDialog changePassDilaog;
    private String enteredUserMobile = "";
    private HashMap<String, String> passMap = new HashMap<>();
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
                        case URLHelper.LOGIN:
                            JSONObject infoObject = object.getJSONObject("user_info");
                            MyPreferenceManager preferenceManager = new MyPreferenceManager(getActivity());
                            preferenceManager.storeUser(infoObject.toString());
                            preferenceManager.setUserLoginStatus(true);
                            Intent i = new Intent(getActivity(), Home.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            break;
                        case URLHelper.GENERATEFORGOTPASSOTP:
                            Helper.showOtpDialog(getActivity(), otpMap, URLHelper.GENERATEFORGOTPASSOTP, enteredUserMobile, NotificationCodes.loginNotificationCode);
                            break;
                        case URLHelper.MATCHOTP:
                            HomeWebService.callCommonWebService(getActivity(), passMap, URLHelper.CHANGEPASS, NotificationCodes.loginNotificationCode);
                            break;
                        case URLHelper.CHANGEPASS:
                            Toast.makeText(getActivity(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                            changePassDilaog.cancel();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_login, null);
        input_mobile = (EditText) view.findViewById(R.id.input_email);
        input_password = (EditText) view.findViewById(R.id.input_password);
        forgot_Password = (TextView) view.findViewById(R.id.forgot_password);
        registerText = (TextView) view.findViewById(R.id.registerText);
        button_login = (FancyButton) view.findViewById(R.id.button_login);
        method_listener();
        return view;
    }

    private void method_listener() {
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginSignUp) getActivity()).changeFragment(new SignupFragment());
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = input_mobile.getText().toString();
                String password = input_password.getText().toString();
                if (!ValidationHelper.validateTelephone(mobile)) {
                    ValidationHelper.setError(input_mobile);
                    Helper.showSnackbar(getView(), "enter valid mobile number");
                } else if (!ValidationHelper.validatePassword(password)) {
                    ValidationHelper.removeError(input_mobile);

                    ValidationHelper.setError(input_password);
                    Helper.showSnackbar(getView(), "enter valid password (min. 6 character)");
                } else {
                    ValidationHelper.removeError(input_password);
                    HashMap<String, String> valueMap = new HashMap<String, String>();
                    valueMap.put(SignUpWebService.mobile, mobile);
                    valueMap.put(SignUpWebService.password, password);
                    HomeWebService.callCommonWebService(getActivity(), valueMap, URLHelper.LOGIN, NotificationCodes.loginNotificationCode);
                }

            }
        });
        forgot_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLocationDialog(null);
            }
        });
    }

    public void showLocationDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_change_password, null);
        builder.setView(view);

        changePassDilaog = builder.create();
        changePassDilaog.show();

        final EditText pass = (EditText) view.findViewById(R.id.input_password);
        final EditText Cpass = (EditText) view.findViewById(R.id.input_con_password);
        final EditText mob = (EditText) view.findViewById(R.id.input_mobile);

        mob.setText(input_mobile.getText().toString());
        mob.setSelection(mob.getText().length());

        FancyButton changePass = (FancyButton) view.findViewById(R.id.button_change_pass);
        changePass.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              String mobile = mob.getText().toString();
                                              String password = pass.getText().toString();
                                              String Cpassword = Cpass.getText().toString();

                                              boolean flag = false;

                                              if (!ValidationHelper.validateTelephone(mobile)) {
                                                  mob.setError("invalid");
                                                  mob.requestFocus();
                                                  flag = true;
                                              } else {
                                                  mob.setError(null);
                                                  enteredUserMobile = mobile;

                                                  if (!ValidationHelper.validatePassword(password)) {
                                                      flag = true;
                                                      pass.setError("invalid");
                                                      pass.requestFocus();

                                                  } else {
                                                      pass.setError(null);
                                                      if (!ValidationHelper.validatePassword(Cpassword)) {
                                                          flag = true;
                                                          Cpass.setError("invalid");
                                                          Cpass.requestFocus();
                                                      } else {
                                                          if (!password.equals(Cpassword)) {
                                                              flag = true;
                                                              Cpass.setError("invalid");
                                                              Cpass.requestFocus();
                                                          } else {
                                                              Cpass.setError(null);
                                                          }
                                                      }
                                                      if (!flag) {
                                                          passMap.put(SignUpWebService.mobile, enteredUserMobile);
                                                          passMap.put(SignUpWebService.password, password);
                                                          otpMap.put(SignUpWebService.mobile, mobile);
                                                          HomeWebService.callCommonWebService(getActivity(), otpMap, URLHelper.GENERATEFORGOTPASSOTP, NotificationCodes.loginNotificationCode);
                                                      }
                                                  }
                                              }
                                          }
                                      }

        );
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.loginNotificationCode));
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }
}