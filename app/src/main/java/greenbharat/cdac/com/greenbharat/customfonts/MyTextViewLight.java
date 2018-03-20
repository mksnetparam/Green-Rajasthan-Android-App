package greenbharat.cdac.com.greenbharat.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lenovo1 on 8/10/2016.
 */
public class MyTextViewLight extends TextView {

    public MyTextViewLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Bariol.ttf");
            setTypeface(tf);
        }
    }
}