package greenbharat.cdac.com.greenbharat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.Select_plant;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.SearchPlant_pojo;

/**
 * Created by CDAC on 8/30/2016.
 */
public class SearchPlant_Adapter extends RecyclerView.Adapter<SearchPlant_Adapter.ContactViewHolder> {

    ArrayList<SearchPlant_pojo> searchPlant_pojos = new ArrayList<SearchPlant_pojo>();
    Context context;

    public SearchPlant_Adapter(Context context, ArrayList<SearchPlant_pojo> searchPlant_pojos) {
        this.context = context;
        this.searchPlant_pojos = searchPlant_pojos;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_plant_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(final ContactViewHolder holder, final int position) {
        SearchPlant_pojo searchPlant_pojo = searchPlant_pojos.get(position);
        holder.text_plant_name.setText(searchPlant_pojo.getPlant_name());
        holder.text_plant_type.setText(searchPlant_pojo.getTree_type());
        if (searchPlant_pojo.getDescription().length() > 300)
            holder.text_plant_details.setText(searchPlant_pojo.getDescription().substring(0, 300) + "....");
        else
            holder.text_plant_details.setText(searchPlant_pojo.getDescription());
        holder.bioNameTextView.setText(searchPlant_pojo.getBiological_name());
        Helper.downloadImage(context, searchPlant_pojo.getImage_url(), holder.imageView, true);
        holder.fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchPlant_pojo item = searchPlant_pojos.get(position);
                String treeName = item.getPlant_name();
                String image_url = item.getImage_url();
                String soilType = item.getSoil_name();
                String treeDetails = item.getDescription();
                Intent intent = new Intent(context, Select_plant.class);
                intent.putExtra("treename", treeName);
                intent.putExtra("image_url", image_url);
                intent.putExtra("soilType", soilType);
                intent.putExtra("seedType", "");
                intent.putExtra("maxHeight", item.getMax_height());
                intent.putExtra("treeDetails", treeDetails);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Pair pair[] = new Pair[]{Pair.create(holder.imageView, ViewCompat.getTransitionName(holder.imageView)), Pair.create(holder.text_plant_name, ViewCompat.getTransitionName(holder.text_plant_name))};
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, pair);
                    context.startActivity(intent, options.toBundle());
                } else
                    context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return searchPlant_pojos.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView text_plant_name, text_plant_type, bioNameTextView, text_plant_details;
        ImageView imageView;
        FancyButton fancyButton;

        public ContactViewHolder(View itemView) {
            super(itemView);
            text_plant_name = (TextView) itemView.findViewById(R.id.text_plant_name);
            bioNameTextView = (TextView) itemView.findViewById(R.id.bioNameTextView);
            text_plant_type = (TextView) itemView.findViewById(R.id.text_plant_type);
            text_plant_details = (TextView) itemView.findViewById(R.id.text_plant_details);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            fancyButton = (FancyButton) itemView.findViewById(R.id.fancy_search_layout);
        }
    }
}
