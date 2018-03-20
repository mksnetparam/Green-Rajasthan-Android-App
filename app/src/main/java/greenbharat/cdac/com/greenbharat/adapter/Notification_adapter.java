package greenbharat.cdac.com.greenbharat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.pojo.Notification_pojo;

public class Notification_adapter extends ArrayAdapter<Notification_pojo> {

    private ArrayList<Notification_pojo> arrayList;
    private Context context;
    private LayoutInflater inflater;

    public Notification_adapter(Context context, ArrayList<Notification_pojo> arrayList) {
        super(context, R.layout.notification_item, arrayList);
        this.arrayList = arrayList;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.notification_item, null, false);
        Notification_pojo notification_pojo = arrayList.get(position);

        TextView textView = (TextView) view.findViewById(R.id.notification_text);
        if (notification_pojo.getIs_read().equals("no")) {
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(null, Typeface.BOLD_ITALIC);
        }

        ImageView image = (ImageView) view.findViewById(R.id.imageView);

        if (notification_pojo.getImage() == null || notification_pojo.getImage().equalsIgnoreCase("")) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(notification_pojo.getImage())
                    .error(R.mipmap.ic_launcher)
//                .fitCenter()
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
        }

        textView.setText(notification_pojo.getTitle());
        return view;
    }
}
