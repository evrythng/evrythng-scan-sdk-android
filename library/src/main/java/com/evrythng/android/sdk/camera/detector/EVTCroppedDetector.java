package com.evrythng.android.sdk.camera.detector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.ByteArrayOutputStream;

/**
 * Decorator class that limits the area in the frame to be scanned by the original detector
 */

public class EVTCroppedDetector<T> extends Detector<T> {

    private final Detector<T> delegate;
    private int boxHeight;
    private int boxWidth;

    /**
     * Detects scannable objects on a box within a specific dimension
     * @param detector - specify the detector to be used.
     * @param boxHeight - height of the crop box
     * @param boxWidth - width of the crop box
     */
    public EVTCroppedDetector(Detector<T> detector, int boxWidth, int boxHeight) {
        this.delegate = detector;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    @Override
    public SparseArray<T> detect(Frame frame) {
        int width = frame.getMetadata().getWidth();
        int height = frame.getMetadata().getHeight();

        //use frame width if boxWidth is > frame width or boxWidth is 0
        if(boxWidth > width || boxWidth == 0) {
            boxWidth = width;
        }

        //use frame height if boxHeight is > frame height or boxHeight is 0
        if(boxHeight > height || boxHeight == 0) {
            boxHeight = height;
        }

        //check if height and with is the same with the specified dimen do not crop frame
        if(width == boxWidth && height == boxHeight)
            return delegate.detect(frame);

        int right = (width / 2) + (boxHeight / 2);
        int left = (width / 2) - (boxHeight / 2);
        int bottom = (height / 2) + (boxWidth / 2);
        int top = (height / 2) - (boxWidth / 2);

        YuvImage yuvImage = new YuvImage(frame.getGrayscaleImageData().array(), ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(left, top, right, bottom), 100, byteArrayOutputStream);
        byte[] jpegArray = byteArrayOutputStream.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.length);

        Frame croppedFrame =
                new Frame.Builder()
                        .setBitmap(bitmap)
                        .setRotation(frame.getMetadata().getRotation())
                        .build();

        return delegate.detect(croppedFrame);
    }

    public boolean isOperational() {
        return delegate.isOperational();
    }

    public boolean setFocus(int id) {
        return delegate.setFocus(id);
    }


}
