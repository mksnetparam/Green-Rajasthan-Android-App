package greenbharat.cdac.com.greenbharat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.database.MySqliteOpenHelper;
import greenbharat.cdac.com.greenbharat.database.NotificaitonTable;
import greenbharat.cdac.com.greenbharat.endpoints.NotificationCodes;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;
import greenbharat.cdac.com.greenbharat.fancybuttons.FancyButton;
import greenbharat.cdac.com.greenbharat.helper.BannerViewHelper;
import greenbharat.cdac.com.greenbharat.helper.BlurImageDownloader;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.JSONHandler;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.helper.NotificationUtils;
import greenbharat.cdac.com.greenbharat.pojo.UserPojo;
import greenbharat.cdac.com.greenbharat.webservices.HomeWebService;

import static greenbharat.cdac.com.greenbharat.R.id.ref_number;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView text_personName, text_personEmail, textViewCategory,text_referal_code;
    LinearLayout llHeader;
    View header;
    NavigationView navigationView;

    String personName, personEmail, personPhotoUrl,referal_code;
    ImageView image_profile;
    RelativeLayout edit_profile;
    FancyButton image_plant_adopt, image_track_progress, image_how_work, image_register_space, image_donate, image_plants;

    MyPreferenceManager myPreferenceManager;
    private TextView toolbarCartItemCount;
    private String reGeneratedfcmID;
    // local brodcast receiver to receive json response
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("1234", "onReceive: dsfadf");
            try {
                JSONObject object = new JSONObject(message);
                String result = object.getString("result");
                if (result.equals("1")) {
                    switch (intent.getStringExtra("url")) {
                        case "refresh":
                            mainTainToolBarCount();
                            break;
                    }
                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                } else if (result.equals("-1")) {
                    Toast.makeText(getApplicationContext(), object.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myPreferenceManager = new MyPreferenceManager(this);
        toolbarCartItemCount = (TextView) findViewById(R.id.count);
        FrameLayout toolbarImage = (FrameLayout) findViewById(R.id.toolbarLayout);
        toolbarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Notification.class);
                startActivity(intent);
            }
        });


        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.cl);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (navigationView.getWidth() * slideOffset);
                cl.setTranslationX(moveFactor);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        new BannerViewHelper().initBanner(this);

        image_plant_adopt = (FancyButton) findViewById(R.id.plant_adopt);
        image_track_progress = (FancyButton) findViewById(R.id.track_progress);
      //  image_how_work = (FancyButton) findViewById(R.id.how_work);
        image_register_space = (FancyButton) findViewById(R.id.register_space);
       // image_donate = (FancyButton) findViewById(R.id.donate);
        image_plants = (FancyButton) findViewById(R.id.plants);

        // call button listener
        method_buttonListener();
        setNavigationHeader();
        // code for test Ad
        updateFCMID();

    }

    private void updateFCMID() {
        if (myPreferenceManager.isLoggedIn()) {
            reGeneratedfcmID = FirebaseInstanceId.getInstance().getToken();
            if (reGeneratedfcmID == null)
                reGeneratedfcmID = "";

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("user_id", myPreferenceManager.getUserPojo().getUser_id());
            map.put("android_key", reGeneratedfcmID);
            HomeWebService.callCommonWebService(this, map, URLHelper.UPDATE_FCM_ID, NotificationCodes.HOME_NOTI);

        }
    }

    private void setNavigationHeader() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);

        text_personName = (TextView) header.findViewById(R.id.text_personName);
        text_personEmail = (TextView) header.findViewById(R.id.text_personEmail);
        image_profile = (ImageView) header.findViewById(R.id.image_profile);
        edit_profile = (RelativeLayout) header.findViewById(R.id.edit_profile);
        text_referal_code = (TextView) header.findViewById(ref_number);
        llHeader = (LinearLayout) header.findViewById(R.id.ll);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myPreferenceManager.isLoggedIn()) {
                    Intent intent = new Intent(Home.this, Edit_profile.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptionsCompat optionsCompat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(Home.this, image_profile, ViewCompat.getTransitionName(image_profile));
                        startActivity(intent, optionsCompat.toBundle());
                    } else
                        startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Login First", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (myPreferenceManager.isLoggedIn()) {
            UserPojo user = null;
            try {
                user = (UserPojo) new JSONHandler().parse(myPreferenceManager.getUser(), UserPojo.class, URLHelper.BEAN_BASE_PACKAGE);
                personName = user.getName();
                personEmail = user.getEmail();
                personPhotoUrl = user.getImage();
                referal_code = user.getReferal_code();

                if (text_personName != null)
                    text_personName.setText(personName);
                if (text_personEmail != null)
                    text_personEmail.setText(personEmail);
                if (personPhotoUrl != null && !personPhotoUrl.equals("")) {
                    Helper.downloadImageNoCache(this, personPhotoUrl, image_profile);

                    new BlurImageDownloader(this, llHeader).execute(personPhotoUrl);
                } else
                    image_profile.setImageResource(R.drawable.profile);
                    text_referal_code.setText(referal_code);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void method_buttonListener() {
        image_plant_adopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Select_plan_pay.class);
                startActivity(intent);
            }
        });

        image_plants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, NatureLoverActivity.class);
                startActivity(intent);
            }
        });

        image_register_space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Register_Space.class);
                startActivity(intent);
            }
        });

        image_track_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, PlantOrderActivity.class);
                startActivity(intent);

            }
        });

  /*      image_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, DonatePlant.class);
                startActivity(intent);
            }
        });*/
     /*   image_how_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, HowWeWork.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(Home.this, Notification.class);
            startActivity(intent);
        } else if (id == R.id.nav_Share) {
            Helper.share(this);
        } else if (id == R.id.nav_rating) {
            rateUs();
        } else if (id == R.id.nav_layout) {
            myPreferenceManager.setUserLoginStatus(false);
            NotificationUtils.clearNotifications();
            Intent i = new Intent(this, SelectWhatToDoActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void shareText(View view) {
        Helper.share(this);
    }

    @Override
    public void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(NotificationCodes.HOME_NOTI));
        mainTainToolBarCount();
        super.onStart();
    }


    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    public void rateUs() {
        Intent feedbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/2LzPFU"));
        startActivity(feedbackIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("1234", "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        mainTainToolBarCount();
    }

    public void mainTainToolBarCount() {
        Cursor cursor = NotificaitonTable.selectUnreadCount(new MySqliteOpenHelper(this).getReadableDatabase(), new String[]{NotificaitonTable.MESAAGE_ID});
        if (cursor != null && cursor.getCount() >= 0) {
            toolbarCartItemCount.setText("" + cursor.getCount());
            Helper.cartBadgeAnimation(Home.this, toolbarCartItemCount);
        }
    }
}