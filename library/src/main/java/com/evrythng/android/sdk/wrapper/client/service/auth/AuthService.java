package com.evrythng.android.sdk.wrapper.client.service.auth;

import com.evrythng.android.sdk.model.Credentials;
import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.core.APIException;
import com.evrythng.android.sdk.wrapper.core.api.ApiService;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.client.service.BaseService;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Response;

/**
 * Created by phillipcui on 6/2/17.
 */

public class AuthService extends BaseService<AuthService> {

    private final ServiceGenerator manager;
    private boolean anonymousUser;
    private ServiceCallback<User> callback;
    private String email, password;
    private User user;
    private boolean createUser;
    private boolean activateUser;

    public AuthService(EVTApiClient client) {
        super(client);
        manager = new ServiceGenerator(this);
    }

    public AuthService createAnonymousUser() {
        anonymousUser = true;
        email = null;
        password = null;
        return this;
    }

    public AuthService createUser(User user) {
        this.user = user;
        createUser = true;
        return this;
    }

    public AuthService validateUser(String userID, String activationCode) {
        activateUser = true;
        this.user = new User();
        user.setUserId(userID);
        user.setActivationCode(activationCode);
        return this;
    }

    public AuthService useCredentials(String email , String password)  {
        this.email = email;
        this.password = password;
        return this;
    }

    public void execute(final ServiceCallback<User> callback) {
        RequestCallback requestCallback = new RequestCallback(manager, callback);
        ApiService service = manager.createService();
        Credentials creds = new Credentials();

        if(anonymousUser) {
            creds.setAnonymous(anonymousUser);
            service.createAnonymousUser(creds).enqueue(requestCallback);
        }
        else if(createUser) {
            service.createUser(user).enqueue(requestCallback);
        }
        else if(activateUser) {
            service.validateUser(user.getUserId(), user).enqueue(requestCallback);
        }
        else {
            creds.setEmail(email);
            creds.setPassword(password);
            service.loginUser(creds).enqueue(requestCallback);
        }
    }

    public User execute() {
        try {
            ApiService service = manager.createService();
            Credentials creds = new Credentials();
            Response<User> response;
            if(anonymousUser) {
                creds.setAnonymous(anonymousUser);
                response = service.createAnonymousUser(creds).execute();
            }
            else if(createUser) {
                response = service.createUser(user).execute();
            }
            else {
                creds.setEmail(email);
                creds.setPassword(password);
                response = service.loginUser(creds).execute();
            }
            if(response != null && response.isSuccessful()) {
                return response.body();
            } else
                throw new APIException(ErrorUtil.parseError(manager, response));
        } catch (SocketTimeoutException e) {
            throw  new APIException(ErrorUtil.parseException(e));
        } catch (IOException e) {
            throw  new APIException(ErrorUtil.parseException(e));
        }
    }

}
