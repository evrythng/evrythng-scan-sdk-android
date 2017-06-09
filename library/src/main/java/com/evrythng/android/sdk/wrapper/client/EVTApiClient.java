package com.evrythng.android.sdk.wrapper.client;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.evrythng.android.sdk.wrapper.client.service.auth.AuthService;
import com.evrythng.android.sdk.wrapper.core.config.ApiConfiguration;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanService;


/**
 * Created by phillipcui on 5/26/17.
 */

public class EVTApiClient {

    private final ApiConfiguration config;

    public EVTApiClient(String apiKey) {
        this(new ApiConfiguration(apiKey));
    }

    EVTApiClient(@NonNull ApiConfiguration config) {
        checkApiConfig(config);
        this.config = config;
    }

    private void checkApiConfig(ApiConfiguration config) {
        if(config == null)
            throw new IllegalArgumentException("Api Configuration should not be null");
        if (TextUtils.isEmpty(config.getUrl())) {
            throw new IllegalStateException(String.format("URL of provided API configuration is invalid: [url=%s]", config.getUrl()));
        }

        if (TextUtils.isEmpty(config.getKey())) {
            throw new IllegalStateException(String.format("API key of provided API configuration is invalid: [key=%s]", config.getKey()));
        }
    }

    public String getUrl() {
        return config.getUrl();
    }

    public String getApiKey() {
        return config.getKey();
    }

    public ScanService scan() {
        return new ScanService(this);
    }

    public AuthService auth() {
        return new AuthService(this);
    }
}
