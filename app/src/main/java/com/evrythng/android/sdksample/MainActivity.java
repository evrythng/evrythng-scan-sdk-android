package com.evrythng.android.sdksample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.evrythng.android.sdk.model.IntentResult;
import com.evrythng.android.sdk.wrapper.client.service.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.client.service.scan.ScanResponse;
import com.evrythng.android.sdk.wrapper.core.APIError;

import java.util.List;

/**
 * Created by ronaldphillipcui on 06/06/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUserId;
    private TextView tvUserKey;
    private TextView tvStatus;
    private TextView tvSocialNetwork;
    private TextView tvResults;
    private Button btnScan;
    private TextView tvEmail;
    private EVTApiClient evtClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserId = (TextView) findViewById(R.id.tv_userId);
        tvUserKey = (TextView) findViewById(R.id.tv_userKey);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvSocialNetwork = (TextView) findViewById(R.id.tv_social_network);
        tvResults = (TextView) findViewById(R.id.tv_results);

        btnScan = (Button) findViewById(R.id.btn_scan);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        tvUserId.setText("ID: " + bundle.getString("userID"));
        tvUserKey.setText("Key: " + bundle.getString("userKey"));
        tvEmail.setText("Email: " + bundle.getString("email"));
        tvStatus.setText("Status: " + bundle.getString("status"));
        tvSocialNetwork.setText("Social Network: " + bundle.getString("socialNetwork"));

        btnScan.setOnClickListener(this);

        evtClient = new EVTApiClient(getString(R.string.api_key));
    }

    @Override
    public void onClick(View v) {
        evtClient.scan().launchScannerCamera(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = evtClient.scan().parseScannerResponse(requestCode, resultCode, data);
        progressDialog = ProgressDialog.show(this, "Identifying product", "Checking item on EVT Platform. Please wait...");
        evtClient.scan().useIntentResult(intentResult).execute(mServiceCallback);
    }


    private ServiceCallback<List<ScanResponse>> mServiceCallback = new ServiceCallback<List<ScanResponse>>() {
        @Override
        public void onResponse(List<ScanResponse> response) {
            progressDialog.dismiss();
        }

        @Override
        public void onFailure(APIError e) {
            progressDialog.dismiss();
            String title = "Unknown Error";
            String message = "Something unexpectedly went wrong.";
            if(e != null) {
                title = e.getType() == APIError.Type.REQUEST ? "Request Failed" : "An Exception occurred";
                String errors = "";
                for(String error : e.getErrors()) {
                    errors += error + "\n";
                }
                if(!TextUtils.isEmpty(errors)) {
                    message = String.format("%s (code: %s)",errors, e.getCode());
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
    };
}
