package greenbharat.cdac.com.greenbharat.adapter;

/**
 * Created by CDAC on 4/27/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.PlantGrowthImagePojo;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<PlantGrowthImagePojo> _imagePaths;
    private LayoutInflater inflater;
    private String base_url;

    // constructor
    public FullScreenImageAdapter(Activity activity, ArrayList<PlantGrowthImagePojo> imagePaths, String base_url) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.base_url = base_url;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_full_screen_item_desing, container,
                false);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.timg);
        Helper.downloadImage(_activity, base_url+ _imagePaths.get(position).getImage(), imgDisplay, true);
        ((ViewPager) container).addView(viewLayout);
        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}