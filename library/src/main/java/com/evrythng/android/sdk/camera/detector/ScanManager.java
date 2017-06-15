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
 * Created by phillipcui on 5/31/17.
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

    public void loadScanConfiguration() {
        //check for any configs sent via intent
        if(activity.getIntent().getExtras() != null) {
            Bundle bundle = activity.getIntent().getExtras();
            scanMode = bundle.getInt(Constants.SCAN_MODE, ScanManager.MODE_BARCODE);
            barcodeFormat = bundle.getInt(Constants.SCAN_FORMAT, Barcode.ALL_FORMATS);
        }
    }

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

        CameraSource.Builder theBuilder = new CameraSource.Builder(context, multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true);
        if(height > 0 && width > 0)
            theBuilder = theBuilder.setRequestedPreviewSize(height, width);
        cameraSource = theBuilder.build();
    }

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

    public void releaseCameraSource() {
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    public void stop() {
        preview.stop();
    }

    public void sendResult(String value, int format) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SCAN_VALUE, value);
        intent.putExtra(Constants.SCAN_FORMAT, format);
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }

    public <T> void setTrackerCallback(TrackerCallback<T> trackerCallback) {
        this.trackerCallback = trackerCallback;
    }

    public int getScanMode() {
        return scanMode;
    }

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

    public void setScanBox(int boxWidth, int boxHeight) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }
}
