package net.androtweet.buddy.customized;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import net.androtweet.buddy.R;

public class CustomToolbar extends Toolbar {

    private final Context mContext;

    public CustomToolbar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CustomToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public CustomToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            View view = inflate(getContext(), R.layout.customtoolbar, null);

//            this.navigationBarLeftImage = (ImageView) view.findViewById(R.id.navigation_bar_left_image);

            addView(view);
        }

    }
}
