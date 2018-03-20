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
import greenbharat.cdac.com.greenbharat.pojo.TreeFriendPojo;

/**
 * Created by CDAC on 8/10/2017.
 */
public class TreeFriendAdapter extends RecyclerView.Adapter<TreeFriendAdapter.ContactViewHolder> {
    private ArrayList<TreeFriendPojo> arrayList = new ArrayList<TreeFriendPojo>();
    private Context context;
    private String base_image_url;

    public TreeFriendAdapter(Context context, ArrayList<TreeFriendPojo> arrayList, String base_image_url) {
        this.context = context;
        this.arrayList = arrayList;
        this.base_image_url = base_image_url;
    }

    @Override
    public TreeFriendAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tree_friend_item, parent, false);
        TreeFriendAdapter.ContactViewHolder contactViewHolder = new TreeFriendAdapter.ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(TreeFriendAdapter.ContactViewHolder holder, final int position) {
        TreeFriendPojo pojo = arrayList.get(position);
        holder.nameTextView.setText(pojo.getName());
        holder.totalPlantsTextView.setText(pojo.getNumber_of_plants());
        holder.totalReferedPlantsTextView.setText(pojo.getNumber_of_referal_plants());
        holder.referalCodeTextView.setText(pojo.getReferal_code());
        Helper.downloadImage(context, base_image_url + pojo.getImage(), holder.imageView, false);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, totalPlantsTextView, referalCodeTextView, totalReferedPlantsTextView;
        ImageView imageView;
        FancyButton fancyButton;

        public ContactViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            totalPlantsTextView = (TextView) itemView.findViewById(R.id.totalPlantsTextView);
            totalReferedPlantsTextView = (TextView) itemView.findViewById(R.id.totalReferedPlantsTextView);
            referalCodeTextView = (TextView) itemView.findViewById(R.id.referalCodeTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            fancyButton = (FancyButton) itemView.findViewById(R.id.fancy_search_layout);
        }
    }
}

