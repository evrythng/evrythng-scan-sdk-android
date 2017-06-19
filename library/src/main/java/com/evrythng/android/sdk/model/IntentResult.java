package com.evrythng.android.sdk.model;

import com.evrythng.android.sdk.wrapper.client.service.scan.ScanMethod;

/**
 * Model for the built-in camera result
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
