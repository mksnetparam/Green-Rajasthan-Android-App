package greenbharat.cdac.com.greenbharat.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.customfonts.MyTextViewLight;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;

public class Splash extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private ImageView imageView;
    private String title = "", message = "", flagType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView) findViewById(R.id.image_logo);
/*        imageView.setBackgroundResource(R.drawable.draw_animation);
        AnimationDrawable rocketAnimation = (AnimationDrawable) imageView.getBackground();//create an AnimationDrawable object
        rocketAnimation.start();*/     // to start the frame animation


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                MyPreferenceManager myPreferenceManager = new MyPreferenceManager(Splash.this);
                if (myPreferenceManager.isLoggedIn()) {
                    Intent i = new Intent(Splash.this, Home.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(Splash.this, SelectWhatToDoActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);

       // getRemoteConfig();
    }

    //get the current version number and name
    private long getVersionCode() {
        String versionName = "";
        long versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("1234", "getVersionInfo: " + versionName + versionCode);
        return versionCode;
    }

    private void getRemoteConfig() {
        final FirebaseRemoteConfig mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(false)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        // cache expiration in seconds
        long cacheExpiration = 3600;
        //expire the cache immediately for development mode.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        // fetch
        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // task successful. Activate the fetched data
                            mRemoteConfig.activateFetched();
                            new MyPreferenceManager(Splash.this).setShareMessage(mRemoteConfig.getString("share_message"));
                            if (mRemoteConfig.getLong("version_code") > getVersionCode()) {
                                View v = Helper.getViewFromXML(Splash.this, R.layout.dialog_banner_zoom);
                                FancyButton fancy_dialog_close = (FancyButton) v.findViewById(R.id.fancy_dialog_close);
                                FancyButton fancy_dialog_update = (FancyButton) v.findViewById(R.id.fancy_dialog_update);
                                fancy_dialog_update.setVisibility(View.VISIBLE);
                                MyTextViewLight title = (MyTextViewLight) v.findViewById(R.id.title);
                                title.setText(mRemoteConfig.getString("title_update"));
                                MyTextViewLight message = (MyTextViewLight) v.findViewById(R.id.message);
                                message.setText(mRemoteConfig.getString("message_update"));
                                final AlertDialog dialog = new AlertDialog.Builder(Splash.this, R.style.MyDialogTheme).setView(v).setCancelable(false).create();
                                fancy_dialog_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                                fancy_dialog_update.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/2LzPFU")));
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                                dialog.show();
                            } else {
                                go();
                            }
                        } else {
                            go();
                        }
                    }
                });
    }

    private void go() {
        MyPreferenceManager myPreferenceManager = new MyPreferenceManager(Splash.this);
        if (myPreferenceManager.isLoggedIn()) {
            Intent i = new Intent(Splash.this, Home.class);
            startActivity(i);
        } else {
            Intent i = new Intent(Splash.this, SelectWhatToDoActivity.class);
            startActivity(i);
        }
        finish();
    }
}