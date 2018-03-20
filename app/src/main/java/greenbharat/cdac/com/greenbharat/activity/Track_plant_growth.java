package greenbharat.cdac.com.greenbharat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.helper.Helper;

public class Track_plant_growth extends AppCompatActivity {

    @InjectView(R.id.plant_image)
    ImageView plant_image;
    @InjectView(R.id.text_view_plantation_date)
    TextView text_view_plantation_date;
    @InjectView(R.id.text_view_last_capture_date)
    TextView text_view_last_capture_date;
    @InjectView(R.id.text_view_land_details)
    TextView text_view_land_details;
    @InjectView(R.id.text_plant_name)
    TextView text_plant_name;
    @InjectView(R.id.text_care_taker_name)
    TextView text_care_taker_name;
    private String plantation_id = "", plantation_date = "", plant_name = "", plant_image_url = "",contact_person="", address="", capture_date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_track_plant_growth);
        ButterKnife.inject(this);
        getSupportActionBar().setTitle("Track your plant growth");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        plantation_id = getIntent().getStringExtra("plantation_id");

        plantation_date = getIntent().getStringExtra("plantation_date");
        text_view_plantation_date.setText(plantation_date);

        plant_name = getIntent().getStringExtra("plant_name");
        text_plant_name.setText(plant_name);

        capture_date = getIntent().getStringExtra("capture_date");
        text_view_last_capture_date.setText(capture_date);

        contact_person = getIntent().getStringExtra("contact_person");
        text_care_taker_name.setText(contact_person);

        address = getIntent().getStringExtra("address");
        text_view_land_details.setText(address);

        plant_image_url = getIntent().getStringExtra("plant_image");
        if (!plant_image_url.equals(""))
            Helper.downloadImage(this, plant_image_url, plant_image, true);
    }

    public void viewPictures(View view) {
        Intent intent = new Intent(this, PlantGrowthChechingActivity.class);
        intent.putExtra("plantation_id", plantation_id);
        startActivity(intent);
    }

    public void viewVideos(View view) {
        Toast.makeText(this, "Coming soon!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}