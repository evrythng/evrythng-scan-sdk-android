package com.evrythng.android.sdk.camera.detector;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by phillipcui on 5/26/17.
 */

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private final TrackerCallback<Barcode> callback;

    public BarcodeTrackerFactory(TrackerCallback<Barcode> callback) {
        this.callback = callback;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new ScanTracker<>(callback);
    }


}
