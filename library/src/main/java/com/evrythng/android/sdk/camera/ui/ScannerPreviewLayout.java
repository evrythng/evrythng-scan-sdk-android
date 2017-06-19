package com.evrythng.android.sdk.camera.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

/**
 * Preview Layout that handles the position of the crop guide
 */

public class ScannerPreviewLayout extends FrameLayout {

    private CustomGridMark gridMark;
    private int boxSize;
    private int width;
    private int height;

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
        //check if width > height if so swap values to make sure resolutions follow portrait mode
        if(width > height) {
            int temp = height;
            height = width;
            width = temp;
        }
        int newHeight = height > 0 ? height : getHeight();
        int newWidth = width > 0 ? width : getWidth();
        int boxLeft = (newWidth/2) - boxSize/2;
        int boxTop = (newHeight/2) - boxSize/2;
        int boxRight = (newWidth/2) + boxSize/2;
        int boxBottom = (newHeight/2) + boxSize/2;

        if(gridMark != null) gridMark.layout(boxLeft, boxTop, boxRight, boxBottom);
    }

    public void addGridMark(int boxSize) {
        this.boxSize = boxSize;
        gridMark = new CustomGridMark(getContext());
        LayoutParams params =
                new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(gridMark, params);
    }

    public void setPreviewSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
