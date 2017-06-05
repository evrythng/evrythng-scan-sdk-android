package com.evrythng.android.sdk.wrapper.client.service.auth;

import com.evrythng.android.sdk.model.Credentials;
import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.service.ApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanResponse;
import com.evrythng.android.sdk.wrapper.core.APIError;
import com.evrythng.android.sdk.wrapper.core.api.ApiService;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.client.service.BaseService;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by phillipcui on 6/2/17.
 */

public class AuthService extends BaseService<AuthService> {

    private final ServiceGenerator manager;
    private boolean anonymousUser;
    private ServiceCallback<User> callback;

    public AuthService(ApiClient client) {
        super(client);
        manager = new ServiceGenerator(this);
    }

    public AuthService createAnonymousUser() {
        anonymousUser = true;
        return this;
    }

    public void execute(ServiceCallback<User> callback) {
        this.callback = callback;
        ApiService service = manager.createService();
        if(anonymousUser) {
            Credentials creds = new Credentials();
            creds.setAnonymous(anonymousUser);
            service.createAnonymousUser(creds).enqueue(mRequestCallback);
        }
    }

    private Callback<User> mRequestCallback = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if(response.isSuccessful()) {
                User user = response.body();
                if(callback != null) callback.onResponse(user);
            } else {
                APIError error = ErrorUtil.parseError(manager, response);
                if(callback != null) callback.onFailure(error);
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            if(callback != null) callback.onFailure(ErrorUtil.parseException(t));
        }
    };


}
