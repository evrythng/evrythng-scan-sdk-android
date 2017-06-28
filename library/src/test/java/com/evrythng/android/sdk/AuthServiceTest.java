package com.evrythng.android.sdk;

import android.util.Log;

import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.auth.AuthService;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.core.APIError;
import com.evrythng.android.sdk.wrapper.core.APIException;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.core.config.ApiConfiguration;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;


/**
 * Created by phillipcui on 6/28/17.
 */

public class AuthServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private EVTApiClient client;
    private MockWebServer mockWebServer;

    @Before
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        ApiConfiguration apiConfiguration = new ApiConfiguration("APIKEY", mockWebServer.url("/").toString());
        client = new EVTApiClient(apiConfiguration);

    }

    @After
    public void tearDown() throws Exception {
        this.mockWebServer.shutdown();
    }

    @Test
    public void AuthService_CreateAnonymousUser_Valid() throws Exception {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.auth().createAnonymousUser().execute();

        RecordedRequest request = this.mockWebServer.takeRequest();

        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("{\"anonymous\":true}", request.getBody().readUtf8());
    }

    @Test
    public void AuthService_LoginUser_Valid() throws Exception {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.auth().useCredentials("Email", "Password").execute();

        RecordedRequest request = this.mockWebServer.takeRequest();

        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("{\"email\":\"Email\",\"password\":\"Password\"}", request.getBody().readUtf8());
    }

    @Test
    public void AuthService_CreateUser_Valid() throws Exception {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        User user = new User();
        user.setEmail("email");
        user.setPassword("password");
        user.setFirstName("firstname");
        user.setLastName("lastname");

        client.auth().createUser(user).execute();

        RecordedRequest request = this.mockWebServer.takeRequest();

        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("{" +
                "\"email\":\"email\"," +
                "\"password\":\"password\"," +
                "\"firstName\":\"firstname\"," +
                "\"lastName\":\"lastname\"}", request.getBody().readUtf8());
    }

    @Test
    public void AuthService_ValidateUser_Valid() throws Exception {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.auth().validateUser("ID", "Activation").execute();

        RecordedRequest request = this.mockWebServer.takeRequest();

        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("{\"evrythngUser\":\"ID\",\"activationCode\":\"Activation\"}", request.getBody().readUtf8());
    }

    @Test
    public void AuthService_LogoutUser_Valid() throws Exception {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        client.auth().logoutUser().execute();

        RecordedRequest request = this.mockWebServer.takeRequest();

        Assert.assertEquals("POST", request.getMethod());
        Assert.assertEquals("APIKEY", request.getHeader("Authorization"));
    }

    @Test
    public void AuthService_NoAction_execute() throws Exception {

        this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));

        thrown.expect(APIException.class);

        client.auth().execute();
    }
}
