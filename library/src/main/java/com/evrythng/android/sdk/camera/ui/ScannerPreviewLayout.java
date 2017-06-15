package com.evrythng.android.sdk.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Created by phillipcui on 6/15/17.
 */

public class ScannerPreviewLayout extends FrameLayout {

    private CustomGridMark gridMark;
    private int boxSize;

    public ScannerPreviewLayout(Context context) {
        super(context);
    }

    public ScannerPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScannerPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e("PreviewLayout", "w x h - " + getWidth() + " - " + getHeight());
        int boxLeft = (getWidth()/2) - boxSize/2;
        int boxTop = (getHeight()/2) - boxSize/2;
        int boxRight = (getWidth()/2) + boxSize/2;
        int boxBottom = (getHeight()/2) + boxSize/2;

        gridMark.layout(boxLeft, boxTop, boxRight, boxBottom);
    }

    public void addGridMark(int boxSize) {
        this.boxSize = boxSize;
        gridMark = new CustomGridMark(getContext());
        LayoutParams params =
                new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(gridMark, params);
    }
}
