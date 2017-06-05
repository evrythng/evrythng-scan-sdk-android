package com.evrythng.android.sdk.camera.detector;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

/**
 * Created by phillipcui on 5/31/17.
 */

public class ScanManager {

    public static final int MODE_BARCODE = 0;

    public static BarcodeDetector generateBarcodeDetector(Context context, int format, TrackerCallback<Barcode> callback) {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(context)
                .setBarcodeFormats(format)
                .build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(callback);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());
        return barcodeDetector;
    }

}
