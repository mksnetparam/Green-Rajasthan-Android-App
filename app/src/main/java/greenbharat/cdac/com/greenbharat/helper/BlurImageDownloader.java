package greenbharat.cdac.com.greenbharat.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.LinearLayout;

import java.io.InputStream;

public class BlurImageDownloader extends AsyncTask<String, Void, Bitmap> {

    LinearLayout bitmap_img;
    boolean isImageDownLodingFailed = false;

    Context context;

    public BlurImageDownloader(Context context, LinearLayout bitmap_img) {
        this.bitmap_img = bitmap_img;
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap new_icon = null;
        try {
            InputStream in_stream = new java.net.URL(url).openStream();
            new_icon = BitmapFactory.decodeStream(in_stream);
        } catch (Exception e) {
            isImageDownLodingFailed = true;  // true this flag when image downloading is failed
        }
        return new_icon;
    }

    protected void onPostExecute(Bitmap result_img) {

        if (result_img != null) {
            Bitmap blurredBitmap = Asd.blur(context, result_img);

            if (!isImageDownLodingFailed)
                bitmap_img.setBackgroundDrawable(new BitmapDrawable(context.getResources(), blurredBitmap));
            else
                bitmap_img.setBackgroundColor(Color.parseColor("#ad24d2ff"));
        }
    }
}
