package com.evrythng.android.sdk;

import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.core.config.ApiConfiguration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by phillipcui on 6/27/17.
 */

public class EVTClientTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void EVTClient_APIConfiguration_isNull() throws IllegalStateException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Api Configuration should not be null");
        ApiConfiguration config = null;
        EVTApiClient client = new EVTApiClient(config);
    }

    @Test
    public void EVTClient_API_KEY_isNull() throws IllegalStateException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("API key of provided API configuration is invalid: [key=null]");
        ApiConfiguration config = new ApiConfiguration(null, "hello");
        EVTApiClient client = new EVTApiClient(config);
    }

    @Test
    public void EVTClient_URL_isNull() throws IllegalStateException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("URL of provided API configuration is invalid: [url=null]");
        ApiConfiguration config = new ApiConfiguration("API_KEY", null);
        EVTApiClient client = new EVTApiClient(config);
    }

    @Test
    public void EVTClient_API_KEY_isEmpty() throws IllegalStateException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("API key of provided API configuration is invalid: [key=]");
        ApiConfiguration config = new ApiConfiguration("", "url");
        EVTApiClient client = new EVTApiClient(config);
    }

    @Test
    public void EVTClient_URL_isEmpty() throws IllegalStateException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("URL of provided API configuration is invalid: [url=]");
        ApiConfiguration config = new ApiConfiguration("API_KEY", "");
        EVTApiClient client = new EVTApiClient(config);
    }
}
