package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.PlantDonorPojo;

/**
 * Created by CDAC on 8/22/2017.
 */

public class PlantDonorAdapter extends RecyclerView.Adapter<PlantDonorAdapter.ContactViewHolder> {

    private ArrayList<PlantDonorPojo> arrayList = new ArrayList<>();
    private Context context;
    private String base_image_url;

    public PlantDonorAdapter(Context context, ArrayList<PlantDonorPojo> arrayList, String base_image_url) {
        this.context = context;
        this.arrayList = arrayList;
        this.base_image_url = base_image_url;
    }

    @Override
    public PlantDonorAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_donor_item, parent, false);
        PlantDonorAdapter.ContactViewHolder contactViewHolder = new PlantDonorAdapter.ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(PlantDonorAdapter.ContactViewHolder holder, final int position) {
        PlantDonorPojo pojo = arrayList.get(position);
        holder.nameTextView.setText(pojo.getName());
        holder.nomineeNameTextView.setText(pojo.getNominee_name());
        holder.totalPlantsTextView.setText(pojo.getNumber_of_plants());
        Helper.downloadImage(context, base_image_url + pojo.getImage(), holder.imageView, false);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, totalPlantsTextView, nomineeNameTextView;
        ImageView imageView;
        FancyButton fancyButton;

        public ContactViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            totalPlantsTextView = (TextView) itemView.findViewById(R.id.totalPlantsTextView);
            nomineeNameTextView = (TextView) itemView.findViewById(R.id.nomineeNameTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            fancyButton = (FancyButton) itemView.findViewById(R.id.fancy_search_layout);
        }
    }
}

