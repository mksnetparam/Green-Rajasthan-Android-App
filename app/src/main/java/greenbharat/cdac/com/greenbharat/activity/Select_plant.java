package greenbharat.cdac.com.greenbharat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.fragment.Medical_benifitFragment;
import greenbharat.cdac.com.greenbharat.fragment.Plant_fragment;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.SearchPlant_pojo;


public class Select_plant extends AppCompatActivity {

    Button button_medical_benifit, button_plant_needs;

    TextView text_plant_name;

    String treeName, image_url, soilType, seedType, treeDetails;
    ImageView imageView;

    public static SearchPlant_pojo searchPlant_pojo = new SearchPlant_pojo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_plant);

        getSupportActionBar().setTitle("Plants Description");


        button_medical_benifit = (Button) findViewById(R.id.button_medical_benifit);
        button_plant_needs = (Button) findViewById(R.id.button_plant_needs);
        text_plant_name = (TextView) findViewById(R.id.text_plant_name);
        imageView = (ImageView) findViewById(R.id.image_banner);


        Intent intent = getIntent();
        treeName = intent.getStringExtra("treename");
        image_url = intent.getStringExtra("image_url");
        soilType = intent.getStringExtra("soilType");
        seedType = intent.getStringExtra("seedType");
        treeDetails = intent.getStringExtra("treeDetails");
        searchPlant_pojo.setDescription(treeDetails);
        searchPlant_pojo.setSoil_name(soilType);
        methodCall();
        Helper.downloadImage(this, image_url, imageView, true);
    }


    private void methodCall() {


        text_plant_name.setText(treeName);

        button_plant_needs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
                button_medical_benifit.setBackgroundResource(R.drawable.selector_new_shapess);
                button_plant_needs.setBackgroundResource(R.drawable.selector_new_shapess1);
                button_plant_needs.setTextColor(Color.WHITE);
                button_medical_benifit.setTextColor(Color.parseColor("#1fb272"));
            }
        });


        button_medical_benifit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(v);
                button_medical_benifit.setBackgroundResource(R.drawable.selector_new_shape);
                button_plant_needs.setBackgroundResource(R.drawable.selector_new_shape1);
                button_medical_benifit.setTextColor(Color.WHITE);
                button_plant_needs.setTextColor(Color.parseColor("#1fb272"));
            }
        });

    }

    public void changeFragment(View view) {
        Fragment fr;

        if (view == findViewById(R.id.button_plant_needs)) {
            fr = new Plant_fragment();

        } else {
            fr = new Medical_benifitFragment();
        }
        Bundle bundle = new Bundle();
        bundle.putString("soilType", soilType);
        fr.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fr);
        fragmentTransaction.commit();
    }

}
