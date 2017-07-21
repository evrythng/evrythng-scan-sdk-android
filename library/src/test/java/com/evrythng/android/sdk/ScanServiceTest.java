package com.evrythng.android.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.test.mock.MockContext;

import com.evrythng.android.sdk.model.IntentResult;
import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanMethod;
import com.evrythng.android.sdk.wrapper.core.config.ApiConfiguration;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * Created by phillipcui on 6/27/17.
 */
public class ScanServiceTest {

    private EVTApiClient client;
    private MockWebServer mockWebServer;

    @Mock
    Context mockContext = new MockContext();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws IOException {
        this.mockWebServer = new MockWebServer();
        this.mockWebServer.start();
        ApiConfiguration apiConfiguration = new ApiConfiguration("APIKEY", mockWebServer.url("/").toString());
        client = new EVTApiClient(apiConfiguration);
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void ScanService_Identity_Barcode_isNull() throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().setMethod(ScanMethod.EAN_13).useValue(null).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        Assert.assertEquals(
                "/scan/identifications?filter=method%3D1d%26type%3Dean_13", request.getPath());
    }

    @Test
    public void ScanService_Identity_Method_isNull()throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useValue("sdsdsd").execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        String actualUrl = ("/scan/identifications?filter=type%3D_%26value%3Dsdsdsd").replace("_", ScanMethod.ALL.getType());
        Assert.assertEquals(actualUrl, request.getPath());
    }

    @Test
    public void ScanService_Identity_Method_Set_isNull()throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useValue("sdsdsd").setMethod(null).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        String actualUrl = ("/scan/identifications?filter=type%3D_%26value%3Dsdsdsd").replace("_", ScanMethod.ALL.getType());
        Assert.assertEquals(actualUrl, request.getPath());
    }

    @Test
    public void ScanService_Identity_Method_All()throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useValue("sdsdsd").execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        String actualUrl = ("/scan/identifications?filter=type%3D_%26value%3Dsdsdsd").replace("_", ScanMethod.ALL.getType());
        Assert.assertEquals(actualUrl, request.getPath());
    }

    @Test
    public void ScanService_Identity_Valid() throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().setMethod(ScanMethod.EAN_13).useValue("sdsdsd").execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        Assert.assertEquals(
                "/scan/identifications?filter=method%3D1d%26type%3Dean_13%26value%3Dsdsdsd",
                request.getPath());
    }

    @Test
    public void ScanService_Method_Multiple_Valid() throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().setMethod(ScanMethod.EAN_13, ScanMethod.EAN_8, ScanMethod.QR_CODE).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        Assert.assertEquals(
                "/scan/identifications?filter=method%3D1d,2d%26type%3Dean_13,ean_8,qr_code",
                request.getPath());
    }

    @Test
    public void ScanService_IntentResult_isNull() throws Exception {

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useIntentResult(null).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        Assert.assertEquals("/scan/identifications?filter=", request.getPath());
    }

    @Test
    public void ScanService_IntentResult_Value_isNull() throws Exception {

        IntentResult result = new IntentResult();
        result.setValue(null);
        result.setScanMethod(ScanMethod.EAN_13);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useIntentResult(result).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        Assert.assertEquals(
                "/scan/identifications?filter=method%3D1d%26type%3Dean_13",
                request.getPath());
    }

    @Test
    public void ScanService_IntentResult_Method_isNull() throws Exception {

        IntentResult result = new IntentResult();
        result.setValue("sdsdsd");
        result.setScanMethod(null);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useIntentResult(result).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        String actualUrl = ("/scan/identifications?filter=type%3D_%26value%3Dsdsdsd").replace("_", ScanMethod.ALL.getType());
        Assert.assertEquals(actualUrl, request.getPath());
    }

    @Test
    public void ScanService_IntentResult_Valid() throws Exception {

        IntentResult result = new IntentResult();
        result.setValue("asdw");
        result.setScanMethod(ScanMethod.EAN_13);

        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("[]"));

        client.scan().useIntentResult(result).execute();

        RecordedRequest request = mockWebServer.takeRequest();

        Assert.assertEquals("GET", request.getMethod());
        Assert.assertEquals(
                "/scan/identifications?filter=method%3D1d%26type%3Dean_13%26value%3Dasdw",
                request.getPath());
    }

    @Test
    public void ScanService_UsePhoto_Path_isNull() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Path should not be null or empty");
        String path = null;
        client.scan().usePhoto(mockContext, path).execute();
    }

    @Test
    public void ScanService_UsePhoto_Path_isEmpty() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Path should not be null or empty");
        String path = " ";
        client.scan().usePhoto(mockContext, path).execute();
    }

    @Test
    public void ScanService_UsePhoto_Path_isInvalid() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("File in path: asds not found");
        Context context = null;
        String path = "asds";
        client.scan().usePhoto(context, path).execute();
    }

    @Test
    public void ScanService_UsePhoto_Context_isNull() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Context should not be null");
        Context context = null;
        Bitmap bitmap = null;
        client.scan().usePhoto(context, bitmap).execute();
    }

    @Test
    public void ScanService_UsePhoto_Bitmap_isNull() throws Exception {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Bitmap should not be null");
        Bitmap bitmap = null;
        client.scan().usePhoto(mockContext, bitmap).execute();
    }
}
