package com.evrythng.android.sdk.wrapper.client.service.auth;

import com.evrythng.android.sdk.model.Credentials;
import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.core.APIException;
import com.evrythng.android.sdk.wrapper.core.api.ApiService;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.client.service.BaseAPIService;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * An API builder class for to send request to the Auth API
 */
public class AuthService extends BaseAPIService {

    private final ServiceGenerator manager;
    private String email, password;
    private User user;
    private int action = 0;
    private final int CREATE_USER = 1;
    private final int CREATE_ANON = 2;
    private final int LOGIN_USER = 4;
    private final int ACTIVATE_USER = 8;
    private final int LOGOUT_USER = 16;

    public AuthService(EVTApiClient client) {
        super(client);
        manager = new ServiceGenerator(this);
    }

    /**
     * Create an anonymous user.
     */
    public AuthService createAnonymousUser() {
        action = CREATE_ANON;
        email = null;
        password = null;
        return this;
    }

    /**
     * Create a new user
     * @param user - contains the required personal info needed to creata a new user.
     */
    public AuthService createUser(User user) {
        action = CREATE_USER;
        this.user = user;
        return this;
    }

    /**
     * Validate the newly created user.
     * @param userID - user Id of the new created user
     * @param activationCode - the activation code needed to validate the new user
     */
    public AuthService validateUser(String userID, String activationCode) {
        action = ACTIVATE_USER;
        this.user = new User();
        user.setUserId(userID);
        user.setActivationCode(activationCode);
        return this;
    }

    /**
     * Used to login to EVT
     * @param email - user's email
     * @param password - user's password
     */
    public AuthService useCredentials(String email , String password)  {
        action = LOGIN_USER;
        this.email = email;
        this.password = password;
        return this;
    }

    /**
     * Log out current user using User API Key
     */
    public AuthService logoutUser() {
        action = LOGOUT_USER;
        return this;
    }

    /**
     * Execute the request containing the set information. This is a synchronous call.
     * Throws an APIException on request failure.
     * @return - the result from the data taken from the scanning API.
     */
    public User execute() throws APIException {
        try {
            Call<User> call = generateCall();
            if(call == null)
                throw new APIException(ErrorUtil.parseException(new IllegalStateException("Could not execute null call object")));

            Response<User> response = call.execute();
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

    /**
     * Execute the request containing the set information. This is an asynchronous call.
     * @param callback - notified when the request is done.
     */
    public void execute(final ServiceCallback<User> callback) {
        RequestCallback requestCallback = new RequestCallback(manager, callback);
        Call<User> call = generateCall();
        if(call == null)
            requestCallback.onFailure(call, new IllegalStateException("Could not execute null call object"));
        call.enqueue(requestCallback);
    }

    private Call<User> generateCall() {
        ApiService service = manager.createService();
        Credentials creds = new Credentials();
        switch (action) {
            case CREATE_ANON:
                creds.setAnonymous(true);
                return service.createAnonymousUser(creds);
            case CREATE_USER:
                return service.createUser(user);
            case ACTIVATE_USER:
                return service.validateUser(user.getUserId(), user);
            case LOGIN_USER:
                creds.setEmail(email);
                creds.setPassword(password);
                return service.loginUser(creds);
            case LOGOUT_USER:
                return service.logoutUser();
        }
        return null;
    }
}
