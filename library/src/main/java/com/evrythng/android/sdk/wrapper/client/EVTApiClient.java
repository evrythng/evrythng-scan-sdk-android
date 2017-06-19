package com.evrythng.android.sdk.wrapper.client;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.evrythng.android.sdk.wrapper.client.service.auth.AuthService;
import com.evrythng.android.sdk.wrapper.core.config.ApiConfiguration;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanService;


/**
 * The EVT API Client wrapper. This is where all the request to the EVT server happens.
 */

public class EVTApiClient {

    private final ApiConfiguration config;

    public EVTApiClient(@NonNull String apiKey) {
        this(new ApiConfiguration(apiKey));
    }

    public EVTApiClient(@NonNull ApiConfiguration config) {
        checkApiConfig(config);
        this.config = config;
    }

    private void checkApiConfig(ApiConfiguration config) {
        if(config == null)
            throw new IllegalArgumentException("Api Configuration should not be null");
        if (config.getUrl() == null || config.getUrl().isEmpty()) {
            throw new IllegalStateException(String.format("URL of provided API configuration is invalid: [url=%s]", config.getUrl()));
        }

        if (config.getKey() == null || config.getKey().isEmpty()) {
            throw new IllegalStateException(String.format("API key of provided API configuration is invalid: [key=%s]", config.getKey()));
        }
    }

    public String getUrl() {
        return config.getUrl();
    }

    public String getApiKey() {
        return config.getKey();
    }

    /**
     * Handles all the scan related API requests
     */
    public ScanService scan() {
        return new ScanService(this);
    }

    /**
     * Handles all the scan related API requests
     */
    public AuthService auth() {
        return new AuthService(this);
    }
}
