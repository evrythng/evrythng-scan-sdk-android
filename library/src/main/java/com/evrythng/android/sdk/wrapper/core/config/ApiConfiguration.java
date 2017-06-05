package com.evrythng.android.sdk.wrapper.core.config;

/**
 * Created by phillipcui on 5/29/17.
 */

public class ApiConfiguration {

    private String url = "https://api.evrythng.com";
    private String key = null;

    public ApiConfiguration(String key) {
        this.key = key;
    }

    public ApiConfiguration(String key, String url) {
        this.key = key;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
