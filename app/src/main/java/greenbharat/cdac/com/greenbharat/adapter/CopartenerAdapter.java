package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.CopartenerPojo;

/**
 * Created by CDAC on 8/10/2017.
 */
public class CopartenerAdapter extends RecyclerView.Adapter<CopartenerAdapter.ContactViewHolder> {
    private ArrayList<CopartenerPojo> arrayList = new ArrayList<CopartenerPojo>();
    private Context context;
    private String base_image_url;

    public CopartenerAdapter(Context context, ArrayList<CopartenerPojo> arrayList, String base_image_url) {
        this.context = context;
        this.arrayList = arrayList;
        this.base_image_url = base_image_url;
    }

    @Override
    public CopartenerAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.copartener_item, parent, false);
        CopartenerAdapter.ContactViewHolder contactViewHolder = new CopartenerAdapter.ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(CopartenerAdapter.ContactViewHolder holder, final int position) {
        final CopartenerPojo pojo = arrayList.get(position);
        holder.nameTextView.setText(pojo.getName());
        holder.totalPlantsTextView.setText(pojo.getNumber_of_plants());
        holder.totalReferedPlantsTextView.setText(pojo.getNumber_of_referal_plants());
        holder.referenceNoTextView.setText(pojo.getReferal_code());
        Helper.downloadImage(context, base_image_url + pojo.getImage(), holder.imageView, false);

        holder.fancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewDialog(pojo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void showViewDialog(CopartenerPojo pojo) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("Name", pojo.getName());
        map.put("Number Of Plants", pojo.getNumber_of_plants());
        map.put("Mobile", pojo.getMobile());
        map.put("Referral Code", pojo.getReferal_code());
        map.put("Address", pojo.getAddress());
        Helper.showTableDialogFromJava(context, map, "Information");
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, totalPlantsTextView, referenceNoTextView, totalReferedPlantsTextView;
        ImageView imageView;
        FancyButton fancyButton;

        public ContactViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            totalPlantsTextView = (TextView) itemView.findViewById(R.id.totalPlantsTextView);
            totalReferedPlantsTextView = (TextView) itemView.findViewById(R.id.totalReferedPlantsTextView);
            referenceNoTextView = (TextView) itemView.findViewById(R.id.referenceNoTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            fancyButton = (FancyButton) itemView.findViewById(R.id.fancy_search_layout);
        }
    }
}
