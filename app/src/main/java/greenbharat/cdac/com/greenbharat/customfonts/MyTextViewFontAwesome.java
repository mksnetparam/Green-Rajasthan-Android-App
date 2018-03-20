package greenbharat.cdac.com.greenbharat.customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by lenovo1 on 8/12/2016.
 */
public class MyTextViewFontAwesome extends TextView {

    public MyTextViewFontAwesome(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewFontAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewFontAwesome(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome.ttf");
            setTypeface(tf);
        }
    }
}