package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.ValidationHelper;
import greenbharat.cdac.com.greenbharat.pojo.OrganizationPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;


public class Select_plan_pay extends AppCompatActivity {

    ArrayList<OrganizationPojo> organizationPojoArrayList = new ArrayList<>();
    String[] organizationArray;
    private EditText input_number, name_nominiee, input_organization, referal_code;
    private RadioButton radioButton_monthly, radioButton_quaterly, radioButton_half_yearly, radioButton_yearly, radioButton_oneTime;
    private FancyButton button_next;
    private int plant_no = 0;
    private int one_plant_cost_from_admin = 0;
    private TextView text_amount, text_plant_cost;
    private MyPreferenceManager myPreferenceManager;
    private String selectedPaymentMode = "", selectedOrganization = "";
    private int total_plant_cost;
    private FrameLayout frame_lay;
    private CheckBox checkboxTerms;
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
                        case URLHelper.GETPLANTCOST:
                            String cost = object.getString("cost");
                            String display_cost = object.getString("display_cost");
                            if (!cost.equals("")) {
                                one_plant_cost_from_admin = Integer.parseInt(cost);
                                text_plant_cost.setText(display_cost);
                            }
                            break;
                        case URLHelper.GETORGANIZATION:
                            handleOrganizations(object);
                            break;
                        case URLHelper.DOADDPLANTORDER:
                            String order_id = object.getString("order_id");
                            Intent i = new Intent(Select_plan_pay.this, SelectPaymentMode.class);
                            i.putExtra("order_id", order_id);
                            i.putExtra("where", "plan");
                            i.putExtra("amount", "" + total_plant_cost);
                            startActivity(i);
                            finish();
                            break;
                    }
                }
                if (result.equals("-1")) {
                    ValidationHelper.setError(referal_code);
                    Helper.showSnackbar(frame_lay, object.getString("msg"));
                } else {
                    Toast.makeText(Select_plan_pay.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_plan_pay);
        getSupportActionBar().setTitle("Select Plan");

        myPreferenceManager = new MyPreferenceManager(this);
        init();
        callRadioButton();
        callMethod();
        callWebService();
    }

    private void init() {
        checkboxTerms = (CheckBox) findViewById(R.id.checkboxTerms);
        frame_lay = (FrameLayout) findViewById(R.id.frame_lay);
        radioButton_monthly = (RadioButton) findViewById(R.id.radio_monthly);
        radioButton_quaterly = (RadioButton) findViewById(R.id.radio_quaterly);
        radioButton_half_yearly = (RadioButton) findViewById(R.id.radio_half_yearly);
        radioButton_yearly = (RadioButton) findViewById(R.id.radio_yearly);
        radioButton_oneTime = (RadioButton) findViewById(R.id.radio_one_time);
        button_next = (FancyButton) findViewById(R.id.button_next);
        input_number = (EditText) findViewById(R.id.input_number);
        name_nominiee = (EditText) findViewById(R.id.name_nominiee);
        text_amount = (TextView) findViewById(R.id.text_amount);
        text_plant_cost = (TextView) findViewById(R.id.text_cost);
        referal_code = (EditText) findViewById(R.id.referal_code);
        input_organization = (EditText) findViewById(R.id.input_organization);
    }

    private void callWebService() {
        HomeWebService.callCommonWebService(this, new HashMap<String, String>(), URLHelper.GETORGANIZATION, NotificationCodes.selectPlant);
        HomeWebService.callCommonWebService(this, new HashMap<String, String>(), URLHelper.GETPLANTCOST, NotificationCodes.selectPlant);
    }

    private void callMethod() {
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameNominiee = name_nominiee.getText().toString().trim();
                String ref_number = referal_code.getText().toString().trim();
                name_nominiee.setText(nameNominiee);
                if (plant_no <= 0) {
                    ValidationHelper.setError(input_number);
                    Helper.showSnackbar(frame_lay, "Enter valid plant number");
                } else if (!ValidationHelper.validateName(nameNominiee.trim())) {
                    ValidationHelper.removeError(input_number);
                    ValidationHelper.setError(name_nominiee);
                    Helper.showSnackbar(frame_lay, "Enter valid name");
                } else if (selectedPaymentMode.equals("")) {
                    ValidationHelper.removeError(name_nominiee);
                    Helper.showSnackbar(frame_lay, "Select Payment Mode");
                } else if (selectedOrganization.equals("")) {
                    Helper.showSnackbar(frame_lay, "Select Organization");
                } else if (!checkboxTerms.isChecked()) {
                    Helper.showSnackbar(frame_lay, "Read & Accept terms");
                    checkboxTerms.requestFocus();
                } else {
                    HashMap<String, String> bundle = new HashMap<String, String>();
                    bundle.put("number_of_plants", "" + plant_no);
                    bundle.put("nominee_name", "" + nameNominiee);
                    bundle.put("payment_mode", "" + selectedPaymentMode);
                    bundle.put("plant_cost", "" + total_plant_cost);
                    bundle.put("organization_id", "" + selectedOrganization);
                    bundle.put("referal_code", ref_number);
                    bundle.put("user_id", new MyPreferenceManager(Select_plan_pay.this).getUserPojo().getUser_id());
                    HomeWebService.callCommonWebService(Select_plan_pay.this, bundle, URLHelper.DOADDPLANTORDER, NotificationCodes.selectPlant);
                }
            }
        });
        input_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals("")) {
                    plant_no = Integer.parseInt(s.toString());
                    calculateCost(selectedPaymentMode);
                    text_amount.setText(String.valueOf(total_plant_cost));
                } else {
                    plant_no = 0;
                    calculateCost(selectedPaymentMode);
                    text_amount.setText(String.valueOf(total_plant_cost));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void goToHowWeWork(View view) {
        startActivity(new Intent(this, HowWeWork.class));
    }

    public void selectCategory(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Organization");
        builder.setItems(organizationArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                input_organization.setText(organizationArray[item]);
                selectedOrganization = organizationPojoArrayList.get(item).getOrganization_id();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void callRadioButton() {
        radioButton_monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_plant_cost = plant_no * one_plant_cost_from_admin;
                text_amount.setText(String.valueOf(total_plant_cost));
                if (radioButton_monthly.isChecked()) {
                    selectedPaymentMode = "Monthly";
                    radioButton_quaterly.setChecked(false);
                    radioButton_yearly.setChecked(false);
                    radioButton_half_yearly.setChecked(false);
                    radioButton_oneTime.setChecked(false);
                }
            }
        });

        radioButton_quaterly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_plant_cost = plant_no * one_plant_cost_from_admin * 3;
                text_amount.setText(String.valueOf(total_plant_cost));
                if (radioButton_quaterly.isChecked()) {
                    selectedPaymentMode = "Quarterly";
                    radioButton_monthly.setChecked(false);
                    radioButton_yearly.setChecked(false);
                    radioButton_half_yearly.setChecked(false);
                    radioButton_oneTime.setChecked(false);
                }
            }
        });

        radioButton_half_yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_plant_cost = plant_no * one_plant_cost_from_admin * 6;
                text_amount.setText(String.valueOf(total_plant_cost));
                if (radioButton_half_yearly.isChecked()) {
                    selectedPaymentMode = "Half Yearly";
                    radioButton_monthly.setChecked(false);
                    radioButton_yearly.setChecked(false);
                    radioButton_quaterly.setChecked(false);
                    radioButton_oneTime.setChecked(false);
                }
            }
        });

        radioButton_yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                total_plant_cost = plant_no * one_plant_cost_from_admin * 12;
                text_amount.setText(String.valueOf(total_plant_cost));
                if (radioButton_yearly.isChecked()) {
                    selectedPaymentMode = "Yearly";
                    radioButton_monthly.setChecked(false);
                    radioButton_quaterly.setChecked(false);
                    radioButton_half_yearly.setChecked(false);
                    radioButton_oneTime.setChecked(false);
                }
            }
        });

        radioButton_oneTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                total_plant_cost = plant_no * 3100;
                text_amount.setText(String.valueOf(total_plant_cost));
                if (radioButton_oneTime.isChecked()) {
                    selectedPaymentMode = "One Time";
                    radioButton_monthly.setChecked(false);
                    radioButton_quaterly.setChecked(false);
                    radioButton_half_yearly.setChecked(false);
                    radioButton_yearly.setChecked(false);
                }
            }
        });
    }

    private void handleOrganizations(JSONObject object) throws Exception {
        JSONObject arrayDefault = object.getJSONObject("default");
        selectedOrganization = arrayDefault.getString("id");
        input_organization.setText(arrayDefault.getString("title"));
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

    private void calculateCost(String mode) {
        switch (mode) {
            case "Monthly":
                total_plant_cost = plant_no * one_plant_cost_from_admin * 1;
                break;
            case "Quarterly":
                total_plant_cost = plant_no * one_plant_cost_from_admin * 3;
                break;
            case "Half Yearly":
                total_plant_cost = plant_no * one_plant_cost_from_admin * 6;
                break;
            case "Yearly":
                total_plant_cost = plant_no * one_plant_cost_from_admin * 12;
                break;
            case "One Time":
                total_plant_cost = plant_no * 3100;
                break;
        }
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(Select_plan_pay.this).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.selectPlant));
        super.onStart();
    }

    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(Select_plan_pay.this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }
}