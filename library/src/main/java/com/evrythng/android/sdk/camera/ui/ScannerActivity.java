package com.evrythng.android.sdk.camera.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.evrythng.android.sdk.R;
import com.evrythng.android.sdk.camera.detector.BarcodeTrackerFactory;
import com.evrythng.android.sdk.camera.detector.ScanManager;
import com.evrythng.android.sdk.camera.detector.TrackerCallback;
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

import static android.R.attr.mode;

/**
 * Created by phillipcui on 5/23/17.
 */

public class ScannerActivity extends AppCompatActivity {

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final String TAG = ScannerActivity.class.getSimpleName();

    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private int barcodeFormat = Barcode.ALL_FORMATS;
    private int scanMode = ScanManager.MODE_BARCODE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        //check for any configs sent via intent
        if(getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            scanMode = bundle.getInt(Constants.SCAN_MODE, ScanManager.MODE_BARCODE);
            barcodeFormat = bundle.getInt(Constants.BarcodeFormat, Barcode.ALL_FORMATS);
        }

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mPreview, "Camera is required to use this feature.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", listener)
                .show();

    }

    /**
     * Creates and starts the camera.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        BarcodeDetector barcodeDetector = null;
        //Code where to handle scanning preferences
        if(scanMode == ScanManager.MODE_BARCODE) {
            barcodeDetector =
                    ScanManager.generateBarcodeDetector(context, barcodeFormat, mBarcodeTrackerCallback);
        }

        MultiDetector multiDetector = new MultiDetector.Builder()
                .add(barcodeDetector)
                .build();

        if (!multiDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Low storage", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Low Storage");
                return;
            }
            Toast.makeText(this, "Detector is not operational", Toast.LENGTH_LONG).show();
        }

        mCameraSource = new CameraSource.Builder(getApplicationContext(), multiDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
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
        builder.setTitle("Multitracker sample")
                .setMessage("No camera permission")
                .setPositiveButton("Ok", listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private TrackerCallback<Barcode> mBarcodeTrackerCallback = new TrackerCallback<Barcode>() {
        @Override
        public void onNewItem(int id, Barcode item) {
            Log.i(TAG, String.format("new item: %d - %s", id, item.displayValue));
            Intent intent = new Intent();
            intent.putExtra(Constants.BarcodeValue, item.rawValue);
            intent.putExtra(Constants.BarcodeFormat, item.format);
            setResult(RESULT_OK, intent);
            finish();
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
