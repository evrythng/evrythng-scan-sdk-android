package com.evrythng.android.sdksample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.service.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.core.APIError;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EVTApiClient client;
    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnLogin;
    private Button btnLogAnonymous;
    private Button btnSignup;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        client = new EVTApiClient(getString(R.string.api_key));

        etEmail = (TextInputLayout) findViewById(R.id.et_email);
        etPassword = (TextInputLayout) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogAnonymous = (Button) findViewById(R.id.btn_login_anonymous);
        btnSignup = (Button) findViewById(R.id.btn_signup);

        btnLogin.setOnClickListener(this);
        btnLogAnonymous.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.btn_login_anonymous:
                doAnonLogin();
                break;
            case R.id.btn_signup:
                doSignup();
                break;
        }
    }

    private void doSignup() {
        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);
    }

    private void doAnonLogin() {
        progressDialog = ProgressDialog.show(this, "Logging in to EVT", "Logging in as an anonymous user");
        client.login().createAnonymousUser().execute(mServiceCallback);
    }

    private void doLogin() {
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        progressDialog = ProgressDialog.show(this, "Logging in to EVT", "Logging in as " + email);
        client.login().useCredentials(email, password).execute(mServiceCallback);
    }

    private ServiceCallback<User> mServiceCallback = new ServiceCallback<User>() {
        @Override
        public void onResponse(User user) {
            progressDialog.dismiss();
            String userID = user.getUserId();
            String userKey = user.getApiKey();
            String status = user.getStatus();
            String socialNetwork = user.getSocialNetwork();
            String email = user.getEmail();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userID", userID);
            intent.putExtra("userKey", userKey);
            intent.putExtra("email", email);
            intent.putExtra("status", status);
            intent.putExtra("socialNetwork", socialNetwork);
            startActivity(intent);
            finish();
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

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
    };

}
