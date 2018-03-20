package greenbharat.cdac.com.greenbharat.helper;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;

public class BannerViewHelper {
    public void initBanner(final Activity context) {
        ViewFlipper viewFlipper = (ViewFlipper) context.findViewById(R.id.viewFlipper);
        ImageView b_img1 = (ImageView) context.findViewById(R.id.b_img1);
        loadImage(context, b_img1, R.drawable.newbanner1);
        ImageView b_img2 = (ImageView) context.findViewById(R.id.b_img2);
        loadImage(context, b_img2, R.drawable.newbanner4);
        ImageView b_img3 = (ImageView) context.findViewById(R.id.b_img3);
        loadImage(context, b_img3, R.drawable.newbanner3);
        ImageView b_img4 = (ImageView) context.findViewById(R.id.b_img4);
        loadImage(context, b_img4, R.drawable.bannner6);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.fad_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.fad_out));
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(5000);
        viewFlipper.startFlipping();

        TextView tv = (TextView) context.findViewById(R.id.tv);
        tv.setSelected(true);

        LinearLayout lin_lay_banner = (LinearLayout) context.findViewById(R.id.lin_lay_banner);
        lin_lay_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v  = Helper.getViewFromXML(context, R.layout.dialog_banner_zoom);
                FancyButton fancy_dialog_close = (FancyButton) v.findViewById(R.id.fancy_dialog_close);
                final AlertDialog dialog = new AlertDialog.Builder(context, R.style.MyDialogTheme).setView(v).create();
                fancy_dialog_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    private void loadImage(Context context, ImageView img, int drawableId) {
        Glide.with(context)
                .load(drawableId)
                .error(R.drawable.banner_3)
                .fitCenter()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img);
    }

}
