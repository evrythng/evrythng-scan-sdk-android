package com.evrythng.android.sdk.camera.detector;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by phillipcui on 6/14/17.
 */

public class TrackerFactory<T> implements MultiProcessor.Factory<T> {

    private final TrackerCallback<T> callback;

    public TrackerFactory(TrackerCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public Tracker<T> create(T barcode) {
        return new ScanTracker<>(callback);
    }
}
