package com.evrythng.android.sdk;

import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.BaseService;
import com.evrythng.android.sdk.wrapper.core.APIError;
import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.core.config.ApiConfiguration;
import com.evrythng.android.sdk.wrapper.util.ErrorUtil;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by phillipcui on 6/12/17.
 */

public class ErrorUtilTest {

    //Instantiate for for testing parsing response error
    EVTApiClient client = new EVTApiClient("API_KEY");
    BaseService service = new BaseService(client) {
        @Override
        public EVTApiClient getClient() {
            return super.getClient();
        }
    };
    ServiceGenerator serviceGenerator = new ServiceGenerator(service);

    @Test
    public void ErrorUtil_apiError_exception_noInternet() {
        IOException ioException = new IOException("No network connection");
        APIError error = ErrorUtil.parseException(ioException);
        Assert.assertEquals( "No network connection",error.getErrors().get(0));
    }

    @Test
    public void ErrorUtil_apiError_exception_requestTimeout() {
        SocketTimeoutException stException = new SocketTimeoutException("Request timeout");
        APIError error = ErrorUtil.parseException(stException);
        Assert.assertEquals("Request timeout", error.getErrors().get(0));
    }

    @Test
    public void ErrorUtil_apiError_exception_unexpectedError() {
        IllegalStateException isException = new IllegalStateException("Sample test");
        APIError error = ErrorUtil.parseException(isException);
        String expected = "Unexpected Error: " + isException.getMessage();
        Assert.assertEquals(expected, error.getErrors().get(0));
    }

    @Test
    public void ErrorUtil_apiError_exception_isNotEmpty() {
        IOException ioException = new IOException("No network connection");
        APIError error = ErrorUtil.parseException(ioException);
        Assert.assertFalse(error.getErrors().isEmpty());
    }

    @Test
    public void ErrorUtil_apiError_exception_isEmpty() {
        IllegalStateException ioException = new IllegalStateException("No network connection");
        APIError error = ErrorUtil.parseException(ioException);
        Assert.assertFalse(error.getErrors().isEmpty());
    }

    @Test
    public void ErrorUtil_apiError_exception_IsNull() {
        APIError error = ErrorUtil.parseException(null);
        Assert.assertEquals("Exception is null", error.getErrors().get(0));
    }


    @Test
    public void ErrorUtil_apiError_parseResponse_validErrorResponse() {
        Response<?> response = Response.error(
                403,
                ResponseBody.create(
                        MediaType.parse("application/json"),
                        "{\n" +
                            "  \"status\": 403,\n" +
                            "  \"errors\": [\n" +
                            "    \"Unauthorized Access.\"\n" +
                            "  ],\n" +
                            "  \"moreInfo\": \"https://developers.evrythng.com\"\n" +
                        "}"
                )
        );
        APIError error = ErrorUtil.parseError(serviceGenerator, response);

        Assert.assertEquals(403, error.getStatus());
        Assert.assertEquals(error.getType(), APIError.Type.REQUEST);
        Assert.assertEquals("Unauthorized Access.", error.getErrors().get(0));
        Assert.assertEquals("https://developers.evrythng.com", error.getMoreInfo());
    }

    @Test
    public void ErrorUtil_apiError_parseResponse_invalidErrorResponse() {
        Response<?> response = Response.error(403,
                ResponseBody.create(
                        MediaType.parse("application/json"),
                        ""
                )
        );

        APIError error = ErrorUtil.parseError(serviceGenerator, response);
        Assert.assertEquals(error.getStatus(), 0);
        Assert.assertEquals(APIError.Type.EXCEPTION, error.getType());
        Assert.assertTrue(error.getErrors().get(0).startsWith("Unexpected Error:"));
        Assert.assertNull(error.getMoreInfo());
    }

    @Test
    public void ErrorUtil_apiError_invalidAPIConfig() {
        ApiConfiguration config = new ApiConfiguration("API_KEY", "htpp:/a/asd");
        EVTApiClient client = new EVTApiClient(config);
        BaseService service = new BaseService(client) {
            @Override
            public EVTApiClient getClient() {
                return super.getClient();
            }
        };
        ServiceGenerator serviceGenerator = new ServiceGenerator(service);

    }
}