package com.evrythng.android.sdk.wrapper.client.service.scan;

import com.google.gson.annotations.Expose;

/**
 * Created by phillipcui on 5/30/17.
 */

public class ScanMeta {

    @Expose
    private String method;

    @Expose
    private String score;

    @Expose
    private String value;

    @Expose
    private String type;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
