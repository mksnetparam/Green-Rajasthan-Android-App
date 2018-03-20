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
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.Suggestplant_pojo;

/**
 * Created by CDAC on 8/30/2016.
 */
public class Suggestplant_Adapter extends RecyclerView.Adapter<Suggestplant_Adapter.ContactViewHolder> {

    ArrayList<Suggestplant_pojo> suggestplant_pojos = new ArrayList<Suggestplant_pojo>();
    Context context;

    public Suggestplant_Adapter(Context context, ArrayList<Suggestplant_pojo> suggestplant_pojos) {
        this.context = context;
        this.suggestplant_pojos = suggestplant_pojos;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggest_plant_item, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);

       /* Animation animation = AnimationUtils.loadAnimation(context, R.anim.swing_up_left);
        view.startAnimation(animation);*/

        return contactViewHolder;
    }


    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        Suggestplant_pojo suggestplant_pojo = suggestplant_pojos.get(position);

        holder.text_plant_name.setText(suggestplant_pojo.getTree_name());
        holder.text_plant_type.setText(suggestplant_pojo.getTree_type());
        holder.date.setText(suggestplant_pojo.getAdded_date());
        Helper.downloadImage(context, suggestplant_pojo.getTree_image(), holder.imageView, true);
    }


    @Override
    public int getItemCount() {
        return suggestplant_pojos.size();
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {


        TextView text_plant_name, text_plant_type, date;
        ImageView imageView;

        public ContactViewHolder(View itemView) {
            super(itemView);

            text_plant_name = (TextView) itemView.findViewById(R.id.text_plant_name);
            date = (TextView) itemView.findViewById(R.id.date);
            text_plant_type = (TextView) itemView.findViewById(R.id.text_plant_type);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
