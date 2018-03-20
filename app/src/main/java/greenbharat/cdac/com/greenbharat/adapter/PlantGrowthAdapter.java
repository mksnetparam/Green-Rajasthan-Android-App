package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.PreviewScreen;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.PlantGrowthImagePojo;

/**
 * Created by CDAC on 4/26/2017.
 */

public class PlantGrowthAdapter extends RecyclerView.Adapter<PlantGrowthAdapter.ContactViewHolder> {

    private ArrayList<PlantGrowthImagePojo> plantGrowthImagePojoArrayList = new ArrayList<>();
    private Context context;
   private String base_url = "";

    public PlantGrowthAdapter(Context context, ArrayList<PlantGrowthImagePojo> plantGrowthImagePojoArrayList, String base_url) {
        this.context = context;
        this.plantGrowthImagePojoArrayList = plantGrowthImagePojoArrayList;
        this.base_url = base_url;
    }

    @Override
    public PlantGrowthAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_plant_growth_item, parent, false);
        PlantGrowthAdapter.ContactViewHolder contactViewHolder = new PlantGrowthAdapter.ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(PlantGrowthAdapter.ContactViewHolder holder, final int position) {
        final PlantGrowthImagePojo plantGrowthImagePojo = plantGrowthImagePojoArrayList.get(position);
        Helper.downloadImage(context, base_url+plantGrowthImagePojo.getImage(), holder.image, true);
        holder.capture_date.setText("Captured On # "+plantGrowthImagePojo.getCapture_date());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(context, PreviewScreen.class);
                i.putExtra("urls", plantGrowthImagePojoArrayList);
                i.putExtra("position", position);
                i.putExtra("base_url", base_url);
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantGrowthImagePojoArrayList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView capture_date;

        public ContactViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            capture_date = (TextView) itemView.findViewById(R.id.capture_date);
        }
    }
}