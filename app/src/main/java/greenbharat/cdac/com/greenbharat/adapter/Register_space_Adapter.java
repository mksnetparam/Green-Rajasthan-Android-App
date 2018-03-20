package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.PlantsAndLand;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.Register_space_pojo;

/**
 * Created by CDAC on 9/23/2016.
 */
public class Register_space_Adapter extends RecyclerView.Adapter<Register_space_Adapter.ContactViewHolder> {

    ArrayList<Register_space_pojo> register_space_pojos = new ArrayList<Register_space_pojo>();
    Context context;

    public Register_space_Adapter(Context context, ArrayList<Register_space_pojo> register_space_pojos) {
        this.context = context;
        this.register_space_pojos = register_space_pojos;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.register_space_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Register_space_pojo register_space_pojo = register_space_pojos.get(position);
        holder.title.setText(register_space_pojo.getOrganization_name()+"#"+register_space_pojo.getLand_id());
        holder.land_area.setText(register_space_pojo.getLand_area()+" "+register_space_pojo.getLand_area_unit());
        holder.date.setText(register_space_pojo.getAdded_date());
        holder.status.setText(register_space_pojo.getIs_verified());
        holder.capacity_of_plants.setText(register_space_pojo.getPlant_capacity_qty()+" plants");
        Helper.downloadImage(context, register_space_pojo.getLand_image_url(), holder.image, true);

        holder.button_alloted_trees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (register_space_pojo.getIs_verified().equals("Approved")) {
                    Intent intent = new Intent(context, PlantsAndLand.class);
                    intent.putExtra("land_id", register_space_pojo.getLand_id());
                    context.startActivity(intent);
                }
                else
                {
                    Toast.makeText(context, "Your land has not been APPROVED Yet.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.button_view_land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewLand(register_space_pojo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return register_space_pojos.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, land_area, date, status,  capacity_of_plants;
        FancyButton button_view_land, button_alloted_trees;

        public ContactViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.organizationName);
            land_area = (TextView) itemView.findViewById(R.id.land_area);
            date = (TextView) itemView.findViewById(R.id.register_date);
            status = (TextView) itemView.findViewById(R.id.status);
            capacity_of_plants = (TextView) itemView.findViewById(R.id.capacity_of_plants);
            button_alloted_trees = (FancyButton) itemView.findViewById(R.id.button_alloted_trees);
            button_view_land = (FancyButton) itemView.findViewById(R.id.button_view_land);
        }
    }
    private void showViewLand(Register_space_pojo pojo)
    {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("Land Area", pojo.getLand_area()+" "+pojo.getLand_area_unit());
        map.put("Plant Capacity", pojo.getPlant_capacity_qty()+" plants");
        map.put("Land Address", pojo.getLand_address());
        map.put("District", pojo.getDistrict());
        map.put("Tehsil", pojo.getTehsil());
        map.put("Soil Type", pojo.getSoil_name());
        map.put("Organization Name", pojo.getOrganization_name());
        map.put("registration Date", pojo.getAdded_date());
        map.put("Status", pojo.getIs_verified());
        map.put("Verifier Review", pojo.getReason());
        map.put("Soil Verification Comments", pojo.getSoil_verification_comments());
        map.put("Contact Person", pojo.getContact_person());
        map.put("Contact Person Relation", pojo.getContact_person_relation());
        map.put("Contact Person Mobile", pojo.getContact_person_mobile());
//        map.put("Land Verifier", pojo.getLand_verified_by());
//        map.put("Soil Verifier", pojo.getSoil_verified_by());

        Helper.showTableDialogFromJava(context, map, "Land Information");
    }
}