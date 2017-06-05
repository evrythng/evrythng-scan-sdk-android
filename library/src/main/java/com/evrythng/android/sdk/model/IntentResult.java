package com.evrythng.android.sdk.model;

import com.evrythng.android.sdk.wrapper.client.service.scan.ScanMethod;

/**
 * Created by phillipcui on 5/31/17.
 */

public class IntentResult {
    private ScanMethod scanMethod;
    private String value;

    public void setScanMethod(ScanMethod scanMethod) {
        this.scanMethod = scanMethod;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ScanMethod getScanMethod() {
        return scanMethod;
    }

    public String getValue() {
        return value;
    }
}
