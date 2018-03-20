package greenbharat.cdac.com.greenbharat.helper;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.customfonts.MyTextViewLight;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.otpservices.SmsListener;
import greenbharat.cdac.com.greenbharat.otpservices.SmsReceiver;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;
import greenbharat.cdac.com.greenbharat.webservices.SignUpWebService;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by lenovo1 on 11/22/2016.
 */

public class Helper {
    private static AlertDialog webDialog;
    private static AlertDialog javadialog;

    public static void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        View snackbarView = snackbar.getView();
        int snackbarTextId = android.support.design.R.id.snackbar_text;
        TextView textView = (TextView) snackbarView.findViewById(snackbarTextId);
        textView.setTextColor(Color.WHITE);
        snackbarView.setBackgroundColor(Color.parseColor("#E50000"));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snackbar.show();
    }

    public static void downloadImage(Context context, String imageURL, ImageView imageView, boolean isErrorImage) {
        int error_image = R.drawable.error_image;
        if (!isErrorImage)
            error_image = R.drawable.profile;

        Glide.with(context)
                .load(imageURL)
                .error(error_image)
//                .fitCenter()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void downloadImageFromDrawable(Context context, int imageURL, ImageView imageView) {
        Glide.with(context)
                .load(imageURL)
                .error(R.drawable.banner_3)
                .fitCenter()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    public static void downloadImageNoCache(Context context, String imageURL, ImageView imageView) {
        Glide.with(context)
                .load(imageURL)
                .error(R.drawable.profile)
                .fitCenter()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(imageView);
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {

        }
    }

    public static View getViewFromXML(Context context, int layoutID) {
        return ((LayoutInflater) (context.getSystemService(LAYOUT_INFLATER_SERVICE))).inflate(layoutID, null, false);
    }

    public static void showTableDialogFromJava(Context context, LinkedHashMap<String, String> map, String title) {
        if (javadialog != null && javadialog.isShowing()) {
            javadialog.cancel();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyDialogTheme);
        final View view = getViewFromXML(context, R.layout.demo_design);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.dialog_linear_container);

        TextView title_dialog = (TextView) view.findViewById(R.id.title_dialog);
        if (title != null) {
            title_dialog.setText(title);
        } else title_dialog.setVisibility(View.GONE);

        Set<String> keys = map.keySet();
        for (String key : keys) {
            View itemView = getViewFromXML(context, R.layout.item_dialog_linear_keyvalue);
            TextView keytextView = (TextView) itemView.findViewById(R.id.text_view_key);
            keytextView.setText(key);
            TextView valuetextView = (TextView) itemView.findViewById(R.id.text_view_value);
            valuetextView.setText(map.get(key));
            container.addView(itemView);
        }
        builder.setView(view);
        javadialog = builder.create();
        FancyButton fancy_close = (FancyButton) view.findViewById(R.id.fancy_dialog_close);
        fancy_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                javadialog.cancel();
            }
        });
        javadialog.show();
    }


    public static void showOtpDialog(final Context context, final HashMap<String, String> otpMap, final String urlResend, final String enteredUserMobile, final String notificationCode) {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = layoutInflater.inflate(R.layout.otp_layout_dialog, null, false);
        final EditText editOtpText = (EditText) dialogView.findViewById(R.id.edit_otp);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        MyTextViewLight resendTextView = (MyTextViewLight) dialogView.findViewById(R.id.text_resent_otp);
        resendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                HomeWebService.callCommonWebService(context, otpMap, urlResend, notificationCode);
            }
        });
        FancyButton submitButton = (FancyButton) dialogView.findViewById(R.id.button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editOtpText.getText().toString().equals("") || editOtpText.getText().toString().isEmpty()) {
                    ValidationHelper.setError(editOtpText);
                } else {
                    ValidationHelper.removeError(editOtpText);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(SignUpWebService.mobile, enteredUserMobile);
                    map.put("otp", editOtpText.getText().toString());
                    HomeWebService.callCommonWebService(context, map, URLHelper.MATCHOTP, notificationCode);
                    dialog.cancel();
                }
            }
        });
        FancyButton cancelButton = (FancyButton) dialogView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                String otp = messageText.substring(messageText.indexOf(":") + 1);
                editOtpText.setText(otp);
            }
        });
    }

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
        return sdf.format(date);
    }

    public static void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void cartBadgeAnimation(Context context, final View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 360f);  // HERE 360 IS THE ANGLE OF ROTATE, YOU CAN USE 90, 180 IN PLACE OF IT,  ACCORDING TO YOURS REQUIREMENT
        animation.setDuration(500); // HERE 500 IS THE DURATION OF THE ANIMATION, YOU CAN INCREASE OR DECREASE ACCORDING TO YOURS REQUIREMENT
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setRepeatCount(2);

        AnimatorSet set = new AnimatorSet();
        set.play(animation)
                .before(ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1.0f).setDuration(500))
                .before(ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1.0f).setDuration(500));
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();
    }

    /*==================================================share button action=========================*/
    public static void share(Context context) {
        if (context != null) {
            MyPreferenceManager myPreferenceManager = new MyPreferenceManager(context);
            String share_message = StringValueHelper.SHARE_TEXT;
            if (share_message != null) {
                UserPojo userPojo = myPreferenceManager.getUserPojo();
                share_message = share_message.replace("##", userPojo.getName());
                share_message = share_message.replace("**", userPojo.getReferal_code());

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, share_message);
                context.startActivity(Intent.createChooser(intent, "Share with"));
            }
        }
    }
}