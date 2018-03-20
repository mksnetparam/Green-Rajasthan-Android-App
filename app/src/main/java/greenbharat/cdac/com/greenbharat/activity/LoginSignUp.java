package greenbharat.cdac.com.greenbharat.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.fragment.LoginFragment;
import greenbharat.cdac.com.greenbharat.fragment.SignupFragment;
import greenbharat.cdac.com.greenbharat.helper.BannerViewHelper;
import greenbharat.cdac.com.greenbharat.helper.Helper;


public class LoginSignUp extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_REQ_CODE = 10;
    private String referral_code="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new BannerViewHelper().initBanner(this);

        String which = getIntent().getStringExtra("which");
        if (which != null && which.equalsIgnoreCase("login"))
        {
            changeFragmentWithOut(new LoginFragment());
        }
        else
        {
            changeFragmentWithOut(new SignupFragment());
        }

        checkPermission(this);
    }

    public void shareText(View view)
    {
        Helper.share(this);
    }

    public void changeFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.containerView, fragment);
        transaction.commit();
    }
    public void changeFragmentWithOut(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.containerView, fragment);
        transaction.commit();
    }

    private static void checkPermission(Activity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, REQUEST_PERMISSION_REQ_CODE);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
