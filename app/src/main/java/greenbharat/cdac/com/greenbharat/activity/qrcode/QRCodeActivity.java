package greenbharat.cdac.com.greenbharat.activity.qrcode;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.BitmapHelper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.SelectImageHelper;
import greenbharat.cdac.com.greenbharat.pojo.SearchPlant_pojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_PERMISSION_REQ_CODE = 121;
    private ZXingScannerView mScannerView;
    private HashMap<String, String> treesHashMap = new HashMap<>();
    private ArrayList<String> list = new ArrayList<>();
    private String scannedBarCode = "", entered_tree_number;
    private String selectedLandId = "";
    private SelectImageHelper helper;
    private ImageView dialog_upload_image;
    private String plantation_id = "";
    private View dialogView;
    private boolean isDialogCancelFromOutside = false;
    private boolean isQRMatched = false, isFlashOn = false;
    private EditText input_tree_number;
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
                        case URLHelper.verifyQR:
                            String status = object.getString("status");
                            String msg = object.getString("msg");
                            if (status.equals("true")) {
                                plantation_id = object.getString("plantation_id");
                                if (mScannerView != null) {
                                    mScannerView.stopCamera();
                                    Toast.makeText(context, "camera stoped", Toast.LENGTH_SHORT).show();
                                }
                                isQRMatched = true;
                                formView();
                                showVarificationDialog();
                            } else {
                                showVarificationFailDialog();
                            }
                            break;
                        case URLHelper.UPLOAD_CAPTUREIMAGE:
                            QRCodeActivity.this.setResult(RESULT_OK);
                            finish();
                            break;
                        case "upload":
                            uploadPlantImage();
                            break;
                    }
                } else {
                    Toast.makeText(QRCodeActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static void checkPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission(this);
//        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(R.layout.layout_qr);
        init();

        getSupportActionBar().setTitle("Scan QR Code");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        callWebService();
        selectedLandId = getIntent().getStringExtra("land_id");
    }

    private void init() {
        mScannerView = (ZXingScannerView) findViewById(R.id.scanner);   // Programmatically initialize the scanner view
        input_tree_number = (EditText) findViewById(R.id.input_tree_number);
    }

    public void flash(View view) {
        mScannerView.stopCamera();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.setAutoFocus(true);
        FancyButton button = (FancyButton) view;
        if (!isFlashOn) {
            button.setBackgroundColor(Color.parseColor("#f74c54"));
            mScannerView.setFlash(true);
            isFlashOn = true;
        } else {
            button.setBackgroundColor(Color.parseColor("#b8ffffff"));
            mScannerView.setFlash(false);
            isFlashOn = false;
        }
        mScannerView.startCamera();          // Start camera on resume
    }

    private void formView() {
        dialogView = ((LayoutInflater) (getSystemService(LAYOUT_INFLATER_SERVICE))).inflate(R.layout.dialog_qr_scan_success, null, false);
        dialog_upload_image = (ImageView) dialogView.findViewById(R.id.dialog_upload_image);
        helper = new SelectImageHelper(this, dialog_upload_image, true);
//        helper.setSqureCropEnabled(true);
        FancyButton button = (FancyButton) dialogView.findViewById(R.id.button_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.selectImageCameraOption();
            }
        });
        FancyButton btn_cancel = (FancyButton) dialogView.findViewById(R.id.btn_close_dialog);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void callWebService() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tree_type", "");
        map.put("plant_name", "");
        HomeWebService.callCommonWebService(this, map, URLHelper.GETPLANTS, NotificationCodes.QRCODENOTI);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isQRMatched) {
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.setAutoFocus(true);
            mScannerView.startCamera();
            input_tree_number.setText("");
            // Start camera on resume
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isQRMatched)
            mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v("1234", rawResult.getText()); // Prints scan results
        Log.v("1234", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        scannedBarCode = rawResult.getText();
        if (list.size() != 0)
            showSearchDialog("Select Plant Name You Planted", list);
        else
            mScannerView.resumeCameraPreview(QRCodeActivity.this);
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

    public void done(View view) {
        entered_tree_number = input_tree_number.getText().toString();
        if (!entered_tree_number.equalsIgnoreCase("")) {
            if (list.size() != 0)
                showSearchDialog("Select Plant Name You Planted", list);
            else
                mScannerView.resumeCameraPreview(QRCodeActivity.this);
        }
    }

    private void showSearchDialog(final String title, final ArrayList<String> list) {
        isDialogCancelFromOutside = false;
        View view = ((LayoutInflater) QRCodeActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dailog_search_list, null, false);
        final AlertDialog dialog = new AlertDialog.Builder(QRCodeActivity.this)
                .setTitle(title)
                .setView(view).create();

        ListView lv = (ListView) view.findViewById(R.id.listViewDialog);
        EditText inputSearch = (EditText) view.findViewById(R.id.editTextText);
        final ArrayAdapter adapter = new ArrayAdapter<String>(QRCodeActivity.this, R.layout.list_item, R.id.product_name, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isDialogCancelFromOutside = true;
                String entry = (String) parent.getAdapter().getItem(position);
                String plant_id = treesHashMap.get(entry);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("plant_id", plant_id);
                if (!scannedBarCode.equalsIgnoreCase(""))
                    map.put("qr_code", scannedBarCode);
                else
                    map.put("tree_number", entered_tree_number);
                map.put("land_id", selectedLandId);
                HomeWebService.callCommonWebService(QRCodeActivity.this, map, URLHelper.verifyQR, NotificationCodes.QRCODENOTI);
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
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (!isDialogCancelFromOutside)
                    mScannerView.resumeCameraPreview(QRCodeActivity.this);
            }
        });
        dialog.show();
    }

    public void uploadPlantImage() {
        String imageData = "";
        if (helper.isImageSelected() == true) {
            Bitmap bitmap = new BitmapHelper(this).getThumbnail(helper.getURI_FOR_SELECTED_IMAGE());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                bitmap = helper.rotateBitmap(bitmap, helper.getURI_FOR_SELECTED_IMAGE().getPath());
            }
            if (bitmap != null)
                imageData = helper.getStringImage(bitmap);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("capture_image", imageData);
        map.put("plantation_id", plantation_id);
        HomeWebService.callCommonWebService(this, map, URLHelper.UPLOAD_CAPTUREIMAGE, NotificationCodes.QRCODENOTI);
    }

    private void showVarificationDialog() {
        isDialogCancelFromOutside = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme);
        builder.setCancelable(false);
        builder.setView(dialogView).show();
    }

    private void showVarificationFailDialog() {
        isDialogCancelFromOutside = false;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme);
        View view = ((LayoutInflater) (getSystemService(LAYOUT_INFLATER_SERVICE))).inflate(R.layout.dialog_qr_scan_fail, null, false);

        builder.setCancelable(false);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        FancyButton button = (FancyButton) view.findViewById(R.id.button_scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                input_tree_number.setText("");
                mScannerView.resumeCameraPreview(QRCodeActivity.this);
            }
        });
        FancyButton btn_cancel = (FancyButton) view.findViewById(R.id.btn_close_dialog);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }

    private void handleGetPlants(JSONObject object) throws Exception {
        JSONArray jsonArray = object.getJSONArray("plants");
        list.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            SearchPlant_pojo searchPlant_pojo = (SearchPlant_pojo) new JSONHandler().parse(jsonObject.toString(), SearchPlant_pojo.class, URLHelper.BEAN_BASE_PACKAGE);
            treesHashMap.put(searchPlant_pojo.getPlant_name(), searchPlant_pojo.getPlant_id());
            list.add(searchPlant_pojo.getPlant_name());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mTreeDetailsReceiver, new IntentFilter(NotificationCodes.QRCODENOTI));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTreeDetailsReceiver);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTreeDetailsReceiver);
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        helper.handleResult(requestCode, resultCode, result);  // call this helper class method
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}