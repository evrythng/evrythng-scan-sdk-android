package com.evrythng.android.sdk.camera.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.evrythng.android.sdk.R;
import com.evrythng.android.sdk.camera.detector.ScanManager;
import com.evrythng.android.sdk.camera.detector.TrackerCallback;
import com.evrythng.android.sdk.model.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanMethod;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.text.TextBlock;

/**
 * Created by phillipcui on 5/23/17.
 */

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = ScannerActivity.class.getSimpleName();
    private CameraSourcePreview mPreview;
    private ScanManager scanManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        final ImageView gridMarks = (ImageView) findViewById(R.id.grid_mark);

        final int boxSize = getResources().getDimensionPixelSize(R.dimen.box_size);

        final ViewTreeObserver viewTreeObserver =
                mPreview.getViewTreeObserver();

        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = mPreview.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                int height = mPreview.getHeight();
                int width = mPreview.getWidth();
                int left = width/2 - boxSize/2;
                int right = width/2 + boxSize/2;
                int top = height/2 - boxSize/2;
                int bottom = height + boxSize/2;
                gridMarks.layout(left, top, right, bottom);
            }
        });


        scanManager = new ScanManager(this, mPreview);
        scanManager.setScanBox(boxSize , boxSize);
        //Check which callback to set based on the scanMode set on the scanner
        scanManager.setTrackerCallback(mBarcodeTrackerCallback);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            scanManager.createCameraSource();
        } else {
            scanManager.requestCameraPermission();
        }
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        scanManager.startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        scanManager.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanManager.releaseCameraSource();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != ScanManager.RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            scanManager.createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("EVT Scanner")
                .setMessage("No camera permission")
                .setPositiveButton("Ok", listener)
                .show();
    }

    private TrackerCallback<Barcode> mBarcodeTrackerCallback = new TrackerCallback<Barcode>() {
        @Override
        public void onNewItem(int id, Barcode item) {
            Log.i(TAG, String.format("new item: %d - %s", id, item.displayValue));
            scanManager.sendResult(item.displayValue, item.format);
        }

        @Override
        public void onUpdate(Detector.Detections<Barcode> detectionsResults, Barcode item) {
            Log.i(TAG, String.format("update: %s", item.displayValue));
        }

        @Override
        public void onMissing(Detector.Detections<Barcode> detectionResults) {
            Log.i(TAG, String.format("missing: " + detectionResults.getDetectedItems().toString()));
        }

        @Override
        public void onDone() {
            Log.i(TAG, "Done");
        }
    };


}
