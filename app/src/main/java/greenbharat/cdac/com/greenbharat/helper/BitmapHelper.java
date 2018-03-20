package greenbharat.cdac.com.greenbharat.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import java.io.InputStream;


/**
 * Created by CDAC on 6/28/2017.
 */

public class BitmapHelper {
    private Context context;

    public BitmapHelper(Context context) {
        this.context = context;
    }

    public Bitmap getThumbnail(Uri uri) {
        InputStream input = null;
        Bitmap bitmap = null;
        try {
            input = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
