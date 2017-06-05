package com.evrythng.android.sdk.camera.detector;

import com.google.android.gms.vision.Detector;

/**
 * Created by phillipcui on 5/26/17.
 */

public interface TrackerCallback<T> {
    void onNewItem(int id, T item);
    void onUpdate(Detector.Detections<T> detectionsResults, T item);
    void onMissing(Detector.Detections<T> detectionResults);
    void onDone();
}
