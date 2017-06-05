package com.evrythng.android.sdk.wrapper.client.service.interfaces;

import com.evrythng.android.sdk.wrapper.core.APIError;

/**
 * Created by phillipcui on 6/2/17.
 */

public interface ServiceCallback<T> {
    void onResponse(T response);
    void onFailure(APIError e);
}