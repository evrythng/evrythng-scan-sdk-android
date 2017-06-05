package com.evrythng.android.sdk.wrapper.client.service;

import android.support.annotation.NonNull;

import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.core.APIError;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by phillipcui on 5/29/17.
 */

public abstract class BaseService<T> {

    private final EVTApiClient client;

    public BaseService(EVTApiClient client) {
        this.client = client;
    }
    public EVTApiClient getClient() {
        return client;
    }

    protected class RequestCallback<T> implements Callback<T> {

        private final ServiceGenerator manager;
        ServiceCallback<T> callback;

        public RequestCallback(@NonNull ServiceGenerator manager, ServiceCallback<T> callback) {
            this.callback = callback;
            this.manager = manager;
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if(response.isSuccessful()) {
                T t = response.body();
                if(callback != null) callback.onResponse(t);
            } else {
                APIError error = ErrorUtil.parseError(manager, response);
                if(callback != null) callback.onFailure(error);
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if(callback != null) callback.onFailure(ErrorUtil.parseException(t));
        }
    }
}
