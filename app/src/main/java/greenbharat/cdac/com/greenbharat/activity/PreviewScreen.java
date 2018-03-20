package greenbharat.cdac.com.greenbharat.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.FullScreenImageAdapter;
import greenbharat.cdac.com.greenbharat.helper.ZoomOutPagerTransformer;
import greenbharat.cdac.com.greenbharat.pojo.PlantGrowthImagePojo;


public class PreviewScreen extends AppCompatActivity {

    @InjectView(R.id.pager)
    ViewPager viewPager;

    private ArrayList<PlantGrowthImagePojo> plantGrowthImagePojoArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_screen);
        ButterKnife.inject(this);

        String base_url = getIntent().getStringExtra("base_url");
        int position = getIntent().getIntExtra("position", 0);
        plantGrowthImagePojoArrayList = (ArrayList<PlantGrowthImagePojo>) getIntent().getSerializableExtra("urls");
        FullScreenImageAdapter fullScreenImageAdapter  = new FullScreenImageAdapter(this, plantGrowthImagePojoArrayList, base_url);
        viewPager.setPageTransformer(true, new ZoomOutPagerTransformer());
        viewPager.setAdapter(fullScreenImageAdapter);
        viewPager.setCurrentItem(position, true);
    }

    public void back(View view) {
        finish();
    }
}
