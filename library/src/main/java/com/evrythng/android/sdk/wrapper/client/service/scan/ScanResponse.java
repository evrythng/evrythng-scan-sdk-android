package com.evrythng.android.sdk.wrapper.client.service.scan;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by phillipcui on 5/29/17.
 */

public class ScanResponse {

    @Expose
    List<ScanResult> results;

    @Expose
    ScanMeta meta;

    public List<ScanResult> getResults() {
        return results;
    }

    public void setResults(List<ScanResult> results) {
        this.results = results;
    }

    public ScanMeta getMeta() {
        return meta;
    }

    public void setMeta(ScanMeta meta) {
        this.meta = meta;
    }

}
