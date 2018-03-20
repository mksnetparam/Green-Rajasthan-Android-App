package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.StringValueHelper;
import greenbharat.cdac.com.greenbharat.helper.ValidationHelper;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;


public class DonatePlant extends AppCompatActivity {
    @InjectView(R.id.input_name)
    EditText input_name;
    @InjectView(R.id.input_no_of_plants)
    EditText input_platns_number;
    @InjectView(R.id.input_mobile)
    EditText input_mobile;
    @InjectView(R.id.input_amount)
    EditText input_amount;
    @InjectView(R.id.referal_code)
    EditText referal_code;
    @InjectView(R.id.button_donate)
    FancyButton button_donate;

    private Intent intentToSelectPayment;


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
                        case URLHelper.VERIFY_REFERAL_CODE:
                            goToSelectPayment();
                            break;
                    }
                } else {
                    ValidationHelper.setError(referal_code);
                    Toast.makeText(DonatePlant.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_donate_plant);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle("Contribution");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadDataWithBaseURL(null, StringValueHelper.DONATION_TEXT, "text/html", "utf-8", null);
        methodCall();
    }

    private void methodCall() {
        button_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donateNow();

            }
        });
        input_platns_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().equals("")) {
                    long l = Long.parseLong(charSequence.toString()) * 200;
                    input_amount.setText("" + l);
                } else {
                    input_amount.setText("0");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void donateNow() {
        String name = input_name.getText().toString();
        String number_of_plants = input_platns_number.getText().toString();
        String mobile = input_mobile.getText().toString();
        String amount = input_amount.getText().toString();
        String referalCode = referal_code.getText().toString();

        boolean isError = false;
        if (!ValidationHelper.validateName(name)) {
            isError = true;
            ValidationHelper.setError(input_name);
        } else {
            ValidationHelper.removeError(input_name);
        }
        if (number_of_plants.equals("")) {
            isError = true;
            ValidationHelper.setError(input_platns_number);
        } else {
            ValidationHelper.removeError(input_platns_number);
        }
        if (!ValidationHelper.validateTelephone(mobile)) {
            isError = true;
            ValidationHelper.setError(input_mobile);
        } else {
            ValidationHelper.removeError(input_mobile);
        }
        if (amount.equals("")) {
            isError = true;
            ValidationHelper.setError(input_amount);
        } else {
            ValidationHelper.removeError(input_amount);
        }
        if (!isError) {
            ValidationHelper.removeError(referal_code);

            intentToSelectPayment = new Intent(DonatePlant.this, SelectPaymentMode.class);
            intentToSelectPayment.putExtra("amount", "" + amount);
            intentToSelectPayment.putExtra("where", "donation");
            intentToSelectPayment.putExtra("name", name);
            intentToSelectPayment.putExtra("number_of_plants", number_of_plants);
            intentToSelectPayment.putExtra("mobile", mobile);
            intentToSelectPayment.putExtra("referal_code", referalCode);

            if (referalCode.equalsIgnoreCase("")) {
                goToSelectPayment();
            } else {
                // check if referral code is valid
                HashMap<String, String> map = new HashMap<>();
                map.put("referal_code", referalCode);
                HomeWebService.callCommonWebService(this, map, URLHelper.VERIFY_REFERAL_CODE, NotificationCodes.DONATEPLANT);
            }
        }
    }

    private void goToSelectPayment() {
        startActivityForResult(intentToSelectPayment, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_donate, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_doner) {
            startActivity(new Intent(this, PlantDonorsActivity.class));
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.DONATEPLANT));
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }
}
