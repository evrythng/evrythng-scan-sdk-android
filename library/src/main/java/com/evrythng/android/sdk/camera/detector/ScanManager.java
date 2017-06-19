package com.evrythng.android.sdk.camera.detector;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evrythng.android.sdk.camera.ui.CameraSourcePreview;
import com.evrythng.android.sdk.model.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Manages whole Scanning Logic. Includes the camera permissions, camera lifecycle,
 * camera preview size, frame rate, crop guide size and result wrapping.
 */

public class ScanManager {

    public static final int MODE_BARCODE = 0;
    private static final String TAG = "ScanManager";
    private static final int RC_HANDLE_GMS = 9001;
    public static final int RC_HANDLE_CAMERA_PERM = 2;

    private int barcodeFormat = Barcode.ALL_FORMATS;
    private int scanMode = ScanManager.MODE_BARCODE;

    private final CameraSourcePreview preview;
    private final Activity activity;
    private CameraSource cameraSource;
    private TrackerCallback trackerCallback;
    private int boxWidth;
    private int boxHeight;

    public ScanManager(@NonNull Activity activity, CameraSourcePreview preview) {
        this.preview = preview;
        this.activity = activity;

        //load scanning configurations set by the developer to be used on the scanner
        loadScanConfiguration();
    }

    /**
     * Generates the barcode detector and set the format to be used
     * @return the barcode detector
     */
    public Detector<Barcode> generateBarcodeDetector(Context context, int format, TrackerCallback callback) {
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(context)
                .setBarcodeFormats(format)
                .build();
        EVTCroppedDetector<Barcode> detector = new EVTCroppedDetector<>(barcodeDetector, boxWidth, boxHeight);

        TrackerFactory<Barcode> barcodeFactory = new TrackerFactory<>(callback);
        detector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        return detector;
    }

    private void loadScanConfiguration() {
        //check for any configs sent via intent
        if(activity.getIntent().getExtras() != null) {
            Bundle bundle = activity.getIntent().getExtras();
            scanMode = bundle.getInt(Constants.SCAN_MODE, ScanManager.MODE_BARCODE);
            barcodeFormat = bundle.getInt(Constants.SCAN_FORMAT, Barcode.ALL_FORMATS);
        }
    }

    /**
     * Create the camera instance
     * @param height - height of the camera preview
     * @param width - width of the camera preview
     * Note: this is not always followed. The Camera still looks for the nearest Preview size supported
     *              by the device.
     * @throws IllegalStateException
     */
    public void createCameraSource(int height, int width) throws IllegalStateException {
        Context context = activity.getApplicationContext();
        MultiDetector.Builder builder = new MultiDetector.Builder();
        //Code where to handle scanning preferences
        if(ScanManager.MODE_BARCODE == scanMode) {
            Detector<Barcode> barcodeDetector =
                    generateBarcodeDetector(context, barcodeFormat, trackerCallback);
            builder.add(barcodeDetector);
        }
        MultiDetector multiDetector = builder.build();

        if (!multiDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = activity.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(activity, "Low storage", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Low Storage");
                return;
            }
            Toast.makeText(activity, "Detector is not operational", Toast.LENGTH_LONG).show();
            return;
        }

        CameraSource.Builder cameraBuilder = new CameraSource.Builder(context, multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true);
        if(height > 0 && width > 0) //set preview size only when height and width is not zero
            cameraBuilder = cameraBuilder.setRequestedPreviewSize(height, width);
        cameraSource = cameraBuilder.build();
    }

    /**
     * Starts the Camera's preview
     */
    public void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity.getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                preview.start(cameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    /**
     * Releases the camera instance
     */
    public void releaseCameraSource() {
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    /**
     * Stops the camera preview
     */
    public void stop() {
        preview.stop();
    }

    /**
     * Sends the result to the calling activity.
     */
    public void sendResult(String value, int format) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SCAN_VALUE, value);
        intent.putExtra(Constants.SCAN_FORMAT, format);
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    /**
     * Specify the tracker callback that will be notified the result of the detector.
     */
    public <T> void setTrackerCallback(TrackerCallback<T> trackerCallback) {
        this.trackerCallback = trackerCallback;
    }

    /**
     * Determine the scan mode of the camera
     * @return
     */
    public int getScanMode() {
        return scanMode;
    }

    /**
     * Request the camera permission
     */
    public void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(activity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(preview, "Camera is required to use this feature.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();
    }

    /**
     * Specify the size of the crop guide
     * @param boxWidth
     * @param boxHeight
     */
    public void setScanBox(int boxWidth, int boxHeight) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    /**
     * Get the current camera instance
     */
    public CameraSource getCameraSource() {
        return cameraSource;
    }
}
