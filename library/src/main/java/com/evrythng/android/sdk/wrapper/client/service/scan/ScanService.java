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
import com.evrythng.android.sdk.wrapper.client.service.ApiClient;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.core.APIError;
import com.evrythng.android.sdk.wrapper.core.APIException;
import com.evrythng.android.sdk.wrapper.core.api.ApiService;
import com.evrythng.android.sdk.wrapper.client.service.BaseService;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by phillipcui on 5/26/17.
 */

public class ScanService extends BaseService<ScanService> {

    private static final int REQUEST_SCAN = 0;
    private final ServiceGenerator manager;
    private String filter;
    private ScanMethod[] scanMethod;
    private int scanOption = 0;
    private int USE_CAMERA = 1;
    private int USE_PHOTO = 2;
    private int USE_IDENTIFY = 4;
    private Bitmap bitmap;
    private String barCode;
    private ServiceCallback<List<ScanResponse>> callback;
    public ScanService(ApiClient client) {
        super(client);
        manager = new ServiceGenerator(this);
    }

    public ScanService setMethod(ScanMethod... method) {
        this.scanMethod = method;
        return this;
    }

    public void launchScannerCamera(Activity activity) {
        int format = 0;
        if(scanMethod != null) {
            for (ScanMethod method : scanMethod) {
                format |= method.getFormat();
            }
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SCAN_MODE, ScanManager.MODE_BARCODE);
        bundle.putInt(Constants.BarcodeFormat, format);
        activity.startActivityForResult(new Intent(activity, ScannerActivity.class), REQUEST_SCAN, bundle);
    }

    public ScanService useIntentResult(IntentResult result) {
        if(result != null) {
            scanMethod = new ScanMethod[] { result.getScanMethod() };
            barCode = result.getValue();
        }
        return this;
    }

    public ScanService usePhoto(@NonNull Context context, String path) {
        return usePhoto(context, BitmapFactory.decodeFile(path));
    }

    public ScanService usePhoto(@NonNull Context context, @NonNull Bitmap bitmap) {
        BarcodeDetector detector =
                new BarcodeDetector.Builder(context)
                        .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                        .build();
        if(!detector.isOperational()){
            throw new IllegalStateException("Could not setup detector");
        }

        if(bitmap != null) {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            if(barcodes.size() > 0) {
                barCode = barcodes.get(0).rawValue;
                scanMethod = new ScanMethod[] { getScanMethod(barcodes.get(0).format) };
            }
        }
        return this;
    }

    public ScanService useIdentify(String barCode) {
        scanOption = USE_IDENTIFY;
        this.barCode = barCode;
        return this;
    }

    public List<ScanResponse> execute()  {
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

    public void execute(ServiceCallback<List<ScanResponse>> callback) {
        this.callback = callback;
        ApiService apiService = manager.createService();
        filter = generateFilter();
        apiService.identify(filter).enqueue(mRequestCallback);
    }

    private String generateFilter() {
        String filter = "";
        if(scanMethod != null) {
            String methods = "";
            String types = "";
            for(ScanMethod method : scanMethod) {
                if(!methods.contains(method.geMethod()))
                    methods += "," + method.geMethod();
                if(!methods.contains(method.getType()))
                    types += "," + method.getType();
            }
            filter = String.format("method=%s&type=%s", methods.substring(1), types.substring(1));
        }
        if(barCode != null)
            filter += String.format(filter.length() == 0 ? "value=%s" : "&value=%s", barCode);
        return filter;
    }

    private retrofit2.Callback<List<ScanResponse>> mRequestCallback = new retrofit2.Callback<List<ScanResponse>>() {
        @Override
        public void onResponse(Call<List<ScanResponse>> call, Response<List<ScanResponse>> response) {
            if(response.isSuccessful()) {
                List<ScanResponse> requestResult = response.body();
                if(callback != null) callback.onResponse(requestResult);
            } else {
                APIError error = ErrorUtil.parseError(manager, response);
                if(callback != null) callback.onFailure(error);
            }
        }

        @Override
        public void onFailure(Call<List<ScanResponse>> call, Throwable t) {
            if(callback != null) callback.onFailure(ErrorUtil.parseException(t));
        }
    };

    public static IntentResult parseScannerResponse(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bundle bundle = data.getExtras();

                String value = bundle.getString(Constants.BarcodeValue, "");
                int format = bundle.getInt(Constants.BarcodeFormat, Barcode.ALL_FORMATS);

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
        return null;
    }

}
