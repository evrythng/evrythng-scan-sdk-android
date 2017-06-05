package com.evrythng.android.sdksample;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.evrythng.android.sdk.model.IntentResult;
import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.service.ApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanResponse;
import com.evrythng.android.sdk.wrapper.core.APIError;

import java.util.List;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
    private ApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        client = new ApiClient("J3FW1rGQ7kviN5F03lhd9bosChnmIO2wz8ECizqKTvrliaiTBTfus2XzpPMDhxchQN2iqxRvMJYzSTX9");

        findViewById(R.id.btn_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.scan().launchScannerCamera(TestActivity.this);
            }
        });

        client.login().createAnonymousUser().execute(new ServiceCallback<User>() {
            @Override
            public void onResponse(User user) {
                Log.e(TAG, "User: " + user.toString());
            }

            @Override
            public void onFailure(APIError e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = client.scan().parseScannerResponse(requestCode, resultCode, data);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<ScanResponse> responses = client.scan()
                        .useIntentResult(result)
                        .execute();
                Log.e(TAG, "response: " + responses.toString());
            }
        });
    }
}
