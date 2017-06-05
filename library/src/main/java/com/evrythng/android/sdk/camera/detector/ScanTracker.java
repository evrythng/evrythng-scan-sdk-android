package com.evrythng.android.sdk.camera.detector;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;

/**
 * Created by phillipcui on 5/26/17.
 */

public class ScanTracker<T> extends Tracker<T> {

    private final TrackerCallback<T> callback;

    public ScanTracker(TrackerCallback<T> callback) {
        this.callback = callback;
    }

    public void onNewItem(int id, T item) {
        if(callback != null) callback.onNewItem(id, item);
    }

    public void onUpdate(Detector.Detections<T> detectionResults, T item) {
        if(callback != null) callback.onUpdate(detectionResults, item);
    }

    public void onMissing(Detector.Detections<T> detectionResults) {
        if(callback != null) callback.onMissing(detectionResults);
    }

    public void onDone() {
        if(callback != null) callback.onDone();
    }
}
