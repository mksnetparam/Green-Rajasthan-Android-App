package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.Track_plant_growth;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.PlantsAndLand_pojo;

public class TrackProgress_adapter extends RecyclerView.Adapter<TrackProgress_adapter.ContactViewHolder> {

    private ArrayList<PlantsAndLand_pojo> plantsAndLand_pojos = new ArrayList<>();
    private Context context;
    private String img_prefix;

    public TrackProgress_adapter(Context context, ArrayList<PlantsAndLand_pojo> plantsAndLand_pojos, String img_prefix) {
        this.img_prefix = img_prefix;
        this.context = context;
        this.plantsAndLand_pojos = plantsAndLand_pojos;
    }

    @Override
    public TrackProgress_adapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plants_and_land_item, parent, false);
        TrackProgress_adapter.ContactViewHolder contactViewHolder = new TrackProgress_adapter.ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(TrackProgress_adapter.ContactViewHolder holder, int position) {
        final PlantsAndLand_pojo plantsAndLand_pojo = plantsAndLand_pojos.get(position);

        if (!plantsAndLand_pojo.getImage().contains("http"))
            Helper.downloadImage(context, img_prefix + plantsAndLand_pojo.getImage(), holder.profile_pic, false);
        else
            Helper.downloadImage(context, plantsAndLand_pojo.getImage(), holder.profile_pic, false);

        if (!plantsAndLand_pojo.getPlant_image().equals(""))
            Helper.downloadImage(context, plantsAndLand_pojo.getPlant_image(), holder.image, true);
        else {
            Helper.downloadImageFromDrawable(context, R.drawable.plant, holder.image);

        }
        holder.plant_name.setText(plantsAndLand_pojo.getPlant_name() + "#" + plantsAndLand_pojo.getPlantation_id());
        holder.adopter_name.setText(plantsAndLand_pojo.getName());
        holder.adopted_date.setText(plantsAndLand_pojo.getAllocation_date());

        if (plantsAndLand_pojo.getStatus().equals("Planted")) {
            holder.lin_lay_planted.setVisibility(View.VISIBLE);
            holder.row_growth_status.setVisibility(View.GONE);
        } else {
            holder.row_growth_status.setVisibility(View.VISIBLE);
            holder.plant_status.setText(plantsAndLand_pojo.getStatus());
            holder.lin_lay_planted.setVisibility(View.GONE);
        }
        if (!plantsAndLand_pojo.getCapture_date().equals("")) {
            holder.growth_row.setVisibility(View.VISIBLE);
            holder.due_date.setText(plantsAndLand_pojo.getDue_date());
        } else if (plantsAndLand_pojo.getStatus().equals("Planted")) {
            holder.growth_row.setVisibility(View.VISIBLE);
            holder.due_date.setText(plantsAndLand_pojo.getPlantation_date());
            holder.due_date.setTextColor(Color.parseColor("#d90e22"));
        } else {
            holder.growth_row.setVisibility(View.GONE);
            holder.due_date.setTextColor(Color.parseColor("#1fb272"));
        }
        if (plantsAndLand_pojo.getIs_expired().equals("TRUE")) {
            holder.due_date.setTextColor(Color.parseColor("#d90e22"));
        }

        holder.card_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Track_plant_growth.class);
                intent.putExtra("plantation_id", plantsAndLand_pojo.getPlantation_id());
                intent.putExtra("plantation_date", plantsAndLand_pojo.getPlantation_date());
                Log.d("1234", "onClick: " + plantsAndLand_pojo.getPlantation_date());
                intent.putExtra("plant_name", plantsAndLand_pojo.getPlant_name());
                intent.putExtra("plant_image", plantsAndLand_pojo.getPlant_image());
                intent.putExtra("contact_person", plantsAndLand_pojo.getContact_person());
                intent.putExtra("address", plantsAndLand_pojo.getAddress());
                intent.putExtra("capture_date", plantsAndLand_pojo.getCapture_date());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return plantsAndLand_pojos.size();
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        CircleImageView profile_pic;
        TextView plant_name, adopter_name, adopted_date, due_date, plant_status;
        TableRow growth_row, row_growth_status;
        FancyButton card_click;
        LinearLayout lin_lay_planted;

        public ContactViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.imageView);
            profile_pic = (CircleImageView) itemView.findViewById(R.id.profile_pic);
            plant_name = (TextView) itemView.findViewById(R.id.text_plant_name);
            adopter_name = (TextView) itemView.findViewById(R.id.adopter_name);
            adopted_date = (TextView) itemView.findViewById(R.id.adopted_date);
            growth_row = (TableRow) itemView.findViewById(R.id.growth_row);
            row_growth_status = (TableRow) itemView.findViewById(R.id.row_growth_status);
            due_date = (TextView) itemView.findViewById(R.id.due_date);
            plant_status = (TextView) itemView.findViewById(R.id.plant_status);
            card_click = (FancyButton) itemView.findViewById(R.id.card_click);
            lin_lay_planted = (LinearLayout) itemView.findViewById(R.id.lin_lay_planted);
        }
    }

}