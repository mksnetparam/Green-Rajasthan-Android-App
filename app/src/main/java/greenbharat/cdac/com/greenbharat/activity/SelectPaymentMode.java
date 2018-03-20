package greenbharat.cdac.com.greenbharat.activity;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.customfonts.MyTextViewFontAwesome;
import greenbharat.cdac.com.greenbharat.customfonts.MyTextViewLight;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.StringValueHelper;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

import static greenbharat.cdac.com.greenbharat.webservices.SignUpWebService.email;

public class SelectPaymentMode extends AppCompatActivity {

    @InjectView(R.id.ll)
    LinearLayout ll;

    boolean isOrderPlaced = false;
    private String name = "", phone = "", number_of_plants = "";
    private String txnId = "", order_id = "", amount = "", payment_mode = "", where = "", referal_code;  //pass amount and order_id to this activity
    private AlertDialog paytmAlertDialog;
    private String TAG = "1234";
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
                        case URLHelper.ADDPAYMENTINFO:
                            String msg = StringValueHelper.confirmationText;
                            if (object.has("msg"))
                                msg = object.getString("msg");
                            if (where != null && where.equals("OrderPlantAdapter"))
                                showAlertDialogIntent(SelectPaymentMode.this, "Confirmation", msg);
                            else
                                showAlertDialogIntent(SelectPaymentMode.this, "Confirmation", msg);
                            break;
                        case URLHelper.ADDDONATIONPAYMENTINFO:
                            String msg1 = StringValueHelper.confirmationText;
                            if (object.has("msg"))
                                msg1 = object.getString("msg");
                            showAlertDialogIntent(SelectPaymentMode.this, "Confirmation", msg1);
                            break;
                    }
                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                } else if (result.equals("-1")) {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment_mode);
        ButterKnife.inject(this);
        getSupportActionBar().setTitle("Select Payment Mode");

        getIntentDataFromPreviousActivity();
    }

    private void getIntentDataFromPreviousActivity() {
        where = getIntent().getStringExtra("where");
        if (where != null) {
            if (where.equalsIgnoreCase("donation")) {
                amount = getIntent().getStringExtra("amount");
                referal_code = getIntent().getStringExtra("referal_code");
                name = getIntent().getStringExtra("name");
                phone = getIntent().getStringExtra("mobile");
                number_of_plants = getIntent().getStringExtra("number_of_plants");
                Log.d(TAG, "getIntentDataFromPreviousActivity: " + number_of_plants);
                txnId = "00#" + System.currentTimeMillis();
                email = name.replace(" ", "") + "@gmail.com";
            } else {
                amount = getIntent().getStringExtra("amount");
                order_id = getIntent().getStringExtra("order_id");
                UserPojo userPojoStored = new MyPreferenceManager(this).getUserPojo();
                name = userPojoStored.getName();
                phone = userPojoStored.getMobile();
                email = userPojoStored.getEmail();
                txnId = userPojoStored.getUser_id() + "#" + System.currentTimeMillis();
                if (email.equals("")) {
                    email = name.replace(" ", "") + "@gmail.com";
                }
            }
        }
    }

    public void payCash(View view) {
        payment_mode = "Offline";
        makeOfflinePayment();
    }

    public void payPaytm(View view) {
        payment_mode = "Paytm";
        makePaytmPayment();
    }

    public void payOnline(View view) {
        payment_mode = "Online";
        makeOnlinePayment();
    }

    private void makeOfflinePayment() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.MyDialogTheme);
        View view = Helper.getViewFromXML(this, R.layout.dialog_offline_payment_design);

        LinearLayout lin_center = (LinearLayout) view.findViewById(R.id.lin_center);
        lin_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SelectPaymentMode.this, NatureLoverActivity.class);
                i.putExtra("which", "copartener");
                startActivity(i);
            }
        });

        final EditText input_transaction_id = (EditText) view.findViewById(R.id.input_transaction_id);
        final EditText input_date = (EditText) view.findViewById(R.id.input_date);
        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                DatePickerDialog datePickerDialog = new DatePickerDialog(SelectPaymentMode.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        input_date.setText("" + day + "/" + (month + 1) + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        final MyTextViewLight textViewAmount = (MyTextViewLight) view.findViewById(R.id.textViewAmount);
        textViewAmount.setText("₹ " + amount);
        FancyButton fancy_dialog_close = (FancyButton) view.findViewById(R.id.fancy_dialog_close);
        fancy_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paytmAlertDialog.cancel();
            }
        });
        FancyButton button_submit = (FancyButton) view.findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredTID = input_transaction_id.getText().toString();
                String enteredDate = input_date.getText().toString();
                if (enteredTID.equals("")) {
                    input_transaction_id.setError("required");
                } else if (enteredDate.equalsIgnoreCase("")) {
                    input_transaction_id.setError(null);
                    input_date.setError("required");
                } else {
                    input_date.setError(null);
                    callWebService("Offline", "", enteredTID, "Not Received", "receipt number has been received for cash payment. Date : " + enteredDate);
                }
            }
        });
        builder.setView(view);
        paytmAlertDialog = builder.create();
        paytmAlertDialog.show();
    }

    private void makePaytmPayment() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.MyDialogTheme);
        View view = Helper.getViewFromXML(this, R.layout.paytm_pay_design);

        final EditText input_transaction_id = (EditText) view.findViewById(R.id.input_transaction_id);
        final EditText input_date = (EditText) view.findViewById(R.id.input_date);
        input_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                DatePickerDialog datePickerDialog = new DatePickerDialog(SelectPaymentMode.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        input_date.setText("" + day + "/" + (month + 1) + "/" + year);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        final MyTextViewLight textViewAmount = (MyTextViewLight) view.findViewById(R.id.textViewAmount);
        textViewAmount.setText("₹ " + amount);
        final MyTextViewLight textViewPaytmMobile = (MyTextViewLight) view.findViewById(R.id.textViewPaytmMobile);
        MyTextViewFontAwesome textViewCopy = (MyTextViewFontAwesome) view.findViewById(R.id.textViewCopy);
        textViewCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(textViewPaytmMobile.getText().toString(), textViewPaytmMobile.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(SelectPaymentMode.this, "copied", Toast.LENGTH_SHORT).show();
            }
        });
        FancyButton fancy_dialog_close = (FancyButton) view.findViewById(R.id.fancy_dialog_close);
        fancy_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paytmAlertDialog.cancel();
            }
        });
        FancyButton button_submit = (FancyButton) view.findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredTID = input_transaction_id.getText().toString();
                String enteredDate = input_date.getText().toString();
                if (enteredTID.equals("")) {
                    input_transaction_id.setError("required");
                } else if (enteredDate.equalsIgnoreCase("")) {
                    input_transaction_id.setError(null);
                    input_date.setError("required");
                } else {
                    input_date.setError(null);
                    callWebService("Paytm", "", enteredTID, "Not Received", "Paytm transaction id and payment date received successfully! Date : " + enteredDate);
                }
            }
        });
        builder.setView(view);
        paytmAlertDialog = builder.create();
        paytmAlertDialog.show();
    }

    private void callWebService(String payment_mode, String result_code, String paymentId, String status, String msg) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("mobile", phone);
        map.put("amount", amount);
        map.put("result_code", result_code);
        map.put("transaction_id", txnId);
        map.put("payment_id", paymentId);
        map.put("status", status);
        map.put("message", msg);
        map.put("donation_source", "Android Mobile-App");
        map.put("payment_type", payment_mode);
        map.put("order_id", order_id);

        if (where.equalsIgnoreCase("donation")) {
            map.put("user_id", new MyPreferenceManager(this).getUserPojo().getUser_id());
            map.put("number_of_plants", number_of_plants);
            map.put("referal_code", referal_code);
            HomeWebService.callCommonWebService(this, map, URLHelper.ADDDONATIONPAYMENTINFO, NotificationCodes.paymentMode);
        } else {
            Log.d(TAG, "callWebService other: " + Arrays.asList(map));
            HomeWebService.callCommonWebService(this, map, URLHelper.ADDPAYMENTINFO, NotificationCodes.paymentMode);
        }
    }

    @Override
    protected void onStop() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    protected void onStart() {
        // register since the activity is started.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.paymentMode));
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void showAlertDialogIntent(final Context context, String title, String message) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title)
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (where.equalsIgnoreCase("donation") || where.equalsIgnoreCase("OrderPlantAdapter"))
                            setResult(RESULT_OK);
                        else
                            startActivity(new Intent(SelectPaymentMode.this, PlantOrderActivity.class));
                        finish();
                    }
                })
                .show();
    }

    // ========================================================= online payment starts here =========================================================================================================================
    // =============================================================================================================================================================================================================

    @Override
    public void onBackPressed() {
        if (isOrderPlaced) {
            if (where != null && where.equals("OrderPlantAdapter")) {
                setResult(RESULT_OK);
                finish();
            } else {
                Intent i = new Intent(this, Home.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        } else {
            finish();
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void makeOnlinePayment() {
        UserPojo userPojo = new MyPreferenceManager(this).getUserPojo();
        double amountToBePaid = 100.0;
        if (isDouble(amount)) {
            amountToBePaid = Double.parseDouble(amount);
        }
        String phone = "8290194904";
        String productName = "Green Bharat";
        String firstName = userPojo.getName();
        txnId = "0nf7" + userPojo.getUser_id() + System.currentTimeMillis();
        String email = "netparam.niteshsharma@gmail.com";
//        String sUrl = "https://test.payumoney.com/mobileapp/payumoney/success.php";
//        String fUrl = "https://test.payumoney.com/mobileapp/payumoney/failure.php";
        String sUrl = "https://www.payumoney.com/mob ileapp/payumoney/success.php";
        String fUrl = "https://www.payumoney.com/mob ileapp/payumoney/failure.php";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = false;
        String key = "UCCKcRkn";
        String merchantId = "5762263";
        //test
       /* String merchantId = "4928174";
        String key = "dRQuiA";*/

        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();
        builder.setAmount(amountToBePaid)
                .setTnxId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(isDebug)
                .setKey(key)
                .setMerchantId(merchantId);

        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();
        // Recommended
        calculateServerSideHashAndInitiatePayment(paymentParam);
    }

    private void calculateServerSideHashAndInitiatePayment(final PayUmoneySdkInitilizer.PaymentParam paymentParam) {
        // Replace your server side hash generator API URL
        String url = URLHelper.PAYMENT;
//        String url = "https://test.payumoney.com/payment/op/calculateHashForTest";
        Toast.makeText(this, "Please wait... Generating hash from server ... ", Toast.LENGTH_LONG).show();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "onResponse: " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(SdkConstants.STATUS)) {
                        String status = jsonObject.optString(SdkConstants.STATUS);
                        if (status != null || status.equals("1")) {

                            String hash = jsonObject.getString(SdkConstants.RESULT);
                            Log.i("app_activity", "Server calculated Hash :  " + hash);

                            paymentParam.setMerchantHash(hash);

                            PayUmoneySdkInitilizer.startPaymentActivityForResult(SelectPaymentMode.this, paymentParam);
                        } else {
                            Toast.makeText(SelectPaymentMode.this, jsonObject.getString(SdkConstants.RESULT), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Toast.makeText(SelectPaymentMode.this, SelectPaymentMode.this.getString(R.string.connect_to_internet), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SelectPaymentMode.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paymentParam.getParams();
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                if (paymentId == null) {
                    paymentId = "";
                }
                String message = data.getStringExtra(SdkConstants.MESSAGE);
                if (message == null) {
                    message = "";
                }
                String result = data.getStringExtra(SdkConstants.RESULT);
                if (result == null) {
                    result = "";
                }
                String status = "Not Received";
                String transaction_details = data.getStringExtra(SdkConstants.TRANSACTION_DETAILS);
                if (transaction_details == null) {
                    transaction_details = "";
                }
                if (!paymentId.equalsIgnoreCase("")) {
                    status = "Received";
                }
                callWebService("Online", result, paymentId, status, message + transaction_details);
            } else if (resultCode == RESULT_CANCELED) {
                // it is called on back pressed.
                Log.i(TAG, "failure");
                showDialogMessageOnline("cancelled");
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");
                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {
                        showDialogMessageOnline("pau failure");
                    } else {
                        showDialogMessageOnline("failure");
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
                showDialogMessageOnline("User returned without login");
            }
        }
    }

    private void showDialogMessageOnline(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Payment");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
