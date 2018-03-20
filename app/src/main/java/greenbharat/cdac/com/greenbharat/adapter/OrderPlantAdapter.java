package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.activity.PlantOrderActivity;
import greenbharat.cdac.com.greenbharat.activity.SelectPaymentMode;
import greenbharat.cdac.com.greenbharat.activity.TrackProgress;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.pojo.PlantOrderPojo;

/**
 * Created by lenovo1 on 12/26/2016.
 */

public class OrderPlantAdapter extends RecyclerView.Adapter<OrderPlantAdapter.ContactViewHolder> {

    private ArrayList<PlantOrderPojo> trackProgress_pojos = new ArrayList<>();
    private Context context;

    public OrderPlantAdapter(Context context, ArrayList<PlantOrderPojo> trackProgress_pojos) {
        this.context = context;
        this.trackProgress_pojos = trackProgress_pojos;
    }

    @Override
    public OrderPlantAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_plant_design, parent, false);
        OrderPlantAdapter.ContactViewHolder contactViewHolder = new OrderPlantAdapter.ContactViewHolder(view);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.swing_up_left);
        view.startAnimation(animation);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(OrderPlantAdapter.ContactViewHolder holder, final int position) {
        final PlantOrderPojo pojo = trackProgress_pojos.get(position);
        holder.numberPlant.setText(pojo.getNumber_of_plants());
        holder.orderNumber.setText("Order No #" + pojo.getOrder_id());
        holder.date.setText(pojo.getOrder_date());
        holder.nominee_textView.setText(pojo.getNominee_name());

        if (pojo.getOrder_status().equals("Processed")) {
            holder.lin_lay_planted.setVisibility(View.VISIBLE);
            holder.tbl_row_status.setVisibility(View.GONE);
        } else {
            holder.lin_lay_planted.setVisibility(View.INVISIBLE);
            holder.tbl_row_status.setVisibility(View.VISIBLE);
            holder.status.setText(pojo.getOrder_status());
        }

        if (pojo.getPayment_status().equals("") || pojo.getPayment_status().equals("Not Received")) {
            holder.payment_status.setTextColor(Color.RED);
            holder.payment_status.setText("Not Received ₹ "+pojo.getPlant_cost());
            holder.lin_lay_payment.setVisibility(View.VISIBLE);
        } else if (pojo.getIs_expired().equals("FALSE")) {
            holder.payment_status.setText("Received");
            holder.lin_lay_payment.setVisibility(View.GONE);
        } else {
            holder.payment_status.setText("Due");
            holder.payment_status.setTextColor(Color.RED);
            holder.lin_lay_payment.setVisibility(View.VISIBLE);
        }

        holder.due_date.setText(pojo.getDue_date());

        holder.lin_lay_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SelectPaymentMode.class);
                i.putExtra("order_id", pojo.getOrder_id());
                i.putExtra("amount", "" + pojo.getPlant_cost());
                i.putExtra("where", "OrderPlantAdapter");
                ((PlantOrderActivity)(context)).startActivityForResult(i);
            }
        });

        holder.button_alloted_trees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pojo.getOrder_status().equals("Not Processed")){
                    Toast.makeText(context, "Your order has not been processed yet.", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(context, TrackProgress.class);
                    intent.putExtra("order_id", pojo.getOrder_id());
                    context.startActivity(intent);
                }
            }
        });
        holder.button_view_land.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewOrder(pojo);
            }
        });
        holder.btn_order_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showViewOrder(pojo);
            }
        });
    }


    @Override
    public int getItemCount() {
        return trackProgress_pojos.size();
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView numberPlant, orderNumber, date, status, payment_status, due_date,nominee_textView;
        FancyButton button_view_land, button_alloted_trees, btn_order_list;
        LinearLayout lin_lay_planted;
        FancyButton lin_lay_payment;
        TableRow tbl_row_status;

        public ContactViewHolder(View itemView) {
            super(itemView);

            numberPlant = (TextView) itemView.findViewById(R.id.numberPlant);
            orderNumber = (TextView) itemView.findViewById(R.id.orderNumber);
            date = (TextView) itemView.findViewById(R.id.date);
            status = (TextView) itemView.findViewById(R.id.status);
            due_date = (TextView) itemView.findViewById(R.id.due_date);
            payment_status = (TextView) itemView.findViewById(R.id.payment_status);
            nominee_textView = (TextView) itemView.findViewById(R.id.nominee_textView);
            button_alloted_trees = (FancyButton) itemView.findViewById(R.id.button_alloted_trees);
            button_view_land = (FancyButton) itemView.findViewById(R.id.button_view_land);
            btn_order_list = (FancyButton) itemView.findViewById(R.id.btn_order_list);
            lin_lay_planted = (LinearLayout) itemView.findViewById(R.id.lin_lay_planted);
            lin_lay_payment = (FancyButton) itemView.findViewById(R.id.lin_lay_payment);
            tbl_row_status = (TableRow) itemView.findViewById(R.id.tbl_row_status);
        }
    }

    private void showViewOrder(PlantOrderPojo pojo) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put("Order ID", pojo.getOrder_id());
        map.put("Number Of Plants", pojo.getNumber_of_plants());
        map.put("Order Cost", pojo.getPlant_cost());
        map.put("Organization Name", pojo.getOrganization_name());
        map.put("Order Date", pojo.getOrder_date());
        map.put("Order Status", pojo.getOrder_status());
        map.put("Payment Order", pojo.getPayment_mode());
        map.put("Last Payment Date", pojo.getLast_payment_date());
        map.put("Last Payment Status", pojo.getPayment_status());
        map.put("Payment Due On", pojo.getDue_date());
        Helper.showTableDialogFromJava(context, map, "Order Information");
    }
}

