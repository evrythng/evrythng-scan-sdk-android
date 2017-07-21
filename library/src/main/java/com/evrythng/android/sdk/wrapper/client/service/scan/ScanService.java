package com.evrythng.android.sdk.wrapper.client.service.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.evrythng.android.sdk.camera.detector.ScanManager;
import com.evrythng.android.sdk.camera.ui.ScannerActivity;
import com.evrythng.android.sdk.model.Constants;
import com.evrythng.android.sdk.model.IntentResult;
import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.core.APIException;
import com.evrythng.android.sdk.wrapper.core.api.ApiService;
import com.evrythng.android.sdk.wrapper.client.service.BaseAPIService;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Response;


/**
 * An API builder class for to send request to the Scan API
 */
public class ScanService extends BaseAPIService {

    private static final int REQUEST_SCAN = 0;
    private final ServiceGenerator manager;
    private String filter;
    private ScanMethod[] scanMethod;
    private String barCode;

    public ScanService(EVTApiClient client) {
        super(client);
        manager = new ServiceGenerator(this);
    }

    /**
     * Specify what type of format you want the scanner to detect when using a scanning API.
     * Not specifying any would try to detect all supported formats.
     *
     * Using this together with the Identification API will specify the format of the given
     * value. See useIndenfity() for more details.
     *
     * @param method - scanning when you want to use.
     *
     */
    public ScanService setMethod(ScanMethod... method) {
        this.scanMethod = method;
        return this;
    }

    /**
     * Launch the built-in barcode scanner.
     * @param activity
     */
    public void launchScannerCamera(Activity activity) {
        int format = 0;
        if(scanMethod != null) {
            for (ScanMethod method : scanMethod) {
                format |= method.getFormat();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SCAN_MODE, ScanManager.MODE_BARCODE);
        bundle.putInt(Constants.SCAN_FORMAT, format);
        activity.startActivityForResult(new Intent(activity, ScannerActivity.class), REQUEST_SCAN, bundle);
    }

    /**
     * This is called in onActivityResult(). Passing the result received from the
     * Barcode scanner (called using launchScanningCamera())
     * @param result the result from the bar scanner.
     */
    public ScanService useIntentResult(IntentResult result) {
        if(result != null) {
            //do not add scan method if result's scan method is null
            if(result.getScanMethod() != null)
                scanMethod = new ScanMethod[] { result.getScanMethod() };
            barCode = result.getValue();
        }
        return this;
    }

    /**
     * Detect barcode within a photo.
     * @param context - the context used to create a Barcode detector instance.
     * @param path - the path of the image.
     * Note: be sure to check if the path is valid.
     */
    public ScanService usePhoto(@NonNull Context context, @NonNull String path) throws IllegalStateException {

        if(path == null || path.trim().isEmpty())
            throw new IllegalStateException("Path should not be null or empty");

        File file = new File(path);

        if(!file.exists())
            throw new IllegalStateException(String.format("File in path: %s not found", path));
        
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return usePhoto(context, bitmap);

    }

    /**
     * Detect barcode within a photo
     * @param context - the context used to create a Barcode detector instance.
     * @param bitmap - bitmap object containing the photo to check for barcode.
     * @return
     */
    public ScanService usePhoto(@NonNull Context context, @NonNull Bitmap bitmap) throws IllegalStateException {

        if(context == null)
            throw new IllegalStateException("Context should not be null");

        if(bitmap == null)
            throw new IllegalStateException("Bitmap should not be null");

        BarcodeDetector.Builder builder =
                new BarcodeDetector.Builder(context);

        //extract the method set
        int format = 0;
        if(scanMethod != null && scanMethod.length > 0) {
            for(ScanMethod method : scanMethod) {
                if(method != null) {
                    format |= method.getFormat();
                }
            }
        }
        BarcodeDetector detector;
        if(format == 0)
            detector = builder.build();
        else
            detector = builder.setBarcodeFormats(format).build();

        if(!detector.isOperational()){
            throw new IllegalStateException("Could not setup detector");
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        if(barcodes != null && barcodes.size() > 0) {
            Barcode barcode = barcodes.valueAt(0);
            if(barcode != null) {
                barCode = barcode.rawValue;
                scanMethod = new ScanMethod[]{getScanMethod(barcode.format)};
            }
        }
        return this;
    }

    /**
     * Used to identify if the input is a valid barcode
     * @param barCode - barcode to check in the EVT API.
     */
    public ScanService useIdentify(String barCode) {
        this.barCode = barCode;
        return this;
    }

    /**
     * Execute the request containing the set information. This is a synchronous call.
     * Throws an APIException on request failure.
     * @return - the result from the data taken from the scanning API.
     */
    public List<ScanResponse> execute() throws APIException {
        try {
            ApiService apiService = manager.createService();
            filter = generateFilter();
            Response<List<ScanResponse>> response = apiService.identify(filter).execute();
            if(response.isSuccessful()) {
                return response.body();
            } else
                throw new APIException(ErrorUtil.parseError(manager, response));
        } catch (SocketTimeoutException e) {
            throw  new APIException(ErrorUtil.parseException(e));
        } catch (IOException e) {
            throw  new APIException(ErrorUtil.parseException(e));
        }
    }

    /**
     * Execute the request containing the set information. This is a synchronous call.
     * @param callback - notified the when request is done.
     */
    public void execute(ServiceCallback<List<ScanResponse>> callback) {
        RequestCallback requestCallback = new RequestCallback(manager, callback);
        ApiService apiService = manager.createService();
        filter = generateFilter();
        apiService.identify(filter).enqueue(requestCallback);
    }

    private String generateFilter() {
        String filter = "";
        if(scanMethod != null) {
            String methods = "";
            String types = "";
            for(ScanMethod method : scanMethod) {
                //additional handling incase a null method is passed to the scanMethod array
                if(method == null || method == ScanMethod.ALL) continue;

                if(!methods.contains(method.getMethod()))
                    methods += "," + method.getMethod();
                if(!methods.contains(method.getType()))
                    types += "," + method.getType();
            }
            if(!methods.isEmpty())
                filter += "method="+methods.substring(1);
            if(!types.isEmpty())
                filter += (!filter.isEmpty() ? "&type=" : "type=") + types.substring(1);
        }
        if(barCode != null)
            filter += String.format(filter.isEmpty() ? "type=%s&value=%s" : "&value=%s", barCode, ScanMethod.ALL.getType());
        return filter;
    }

    /**
     * Used to parse the intent returned from the built-in scanner.
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public static IntentResult parseScannerResponse(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bundle bundle = data.getExtras();

                String value = bundle.getString(Constants.SCAN_VALUE, null);
                int format = bundle.getInt(Constants.SCAN_FORMAT, Barcode.ALL_FORMATS);

                //get the ScanMethod enum based on the returned format
                ScanMethod scanMethod = getScanMethod(format);

                IntentResult result = new IntentResult();
                result.setScanMethod(scanMethod);
                result.setValue(value);
                return result;
            }
        }
        return null;
    }

    private static ScanMethod getScanMethod(int format) {
        for (ScanMethod method : ScanMethod.values())
            if (method.getFormat() == format)
                return method;
        return ScanMethod.ALL;
    }

}
