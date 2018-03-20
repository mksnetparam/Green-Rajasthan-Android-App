package greenbharat.cdac.com.greenbharat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.endpoints.URLHelper;


public class HowWeWork extends AppCompatActivity {

    @InjectView(R.id.webView)
    WebView webView;
    @InjectView(R.id.pb)
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_how_we_work);
        ButterKnife.inject(this);

        getSupportActionBar().setTitle("How We Work");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setWebChromeClient(new WebChromeClient() {
                                       @Override
                                       public void onProgressChanged(WebView view, int newProgress) {
                                           if (newProgress == 100)
                                               pb.setVisibility(View.GONE);
                                       }
                                   }
        );
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(HowWeWork.this, "Network Error", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        webView.loadUrl(URLHelper.TERMS_AND_CONDITION);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }
}
