package com.evrythng.android.sdk.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.evrythng.android.sdk.R;

/**
 * Created by phillipcui on 6/15/17.
 */

public class CustomGridMark extends ImageView {
    private int left, right, top, bottom;

    public CustomGridMark(Context context) {
        super(context);
        setImageResource(R.drawable.grid_box);
    }

    public CustomGridMark(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.grid_box);
    }

    public CustomGridMark(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.drawable.grid_box);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
}
