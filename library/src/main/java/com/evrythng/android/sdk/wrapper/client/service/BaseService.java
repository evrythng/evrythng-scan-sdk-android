package com.evrythng.android.sdk.wrapper.client.service;

/**
 * Created by phillipcui on 5/29/17.
 */

public abstract class BaseService<T> {

    private final ApiClient client;

    public BaseService(ApiClient client) {
        this.client = client;
    }
    public ApiClient getClient() {
        return client;
    }
}
