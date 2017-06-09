package com.evrythng.android.sdksample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.evrythng.android.sdk.model.User;
import com.evrythng.android.sdk.wrapper.client.EVTApiClient;
import com.evrythng.android.sdk.wrapper.client.service.interfaces.ServiceCallback;
import com.evrythng.android.sdk.wrapper.core.APIError;

/**
 * Created by ronaldphillipcui on 06/06/2017.
 */

public class CreateUserActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout etEmail;
    private TextInputLayout etPassword;
    private Button btnCreate;
    private TextInputLayout etFirstName;
    private TextInputLayout etLastName;
    private EVTApiClient evtClient;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        etEmail = (TextInputLayout) findViewById(R.id.et_email);
        etPassword = (TextInputLayout) findViewById(R.id.et_password);
        etFirstName = (TextInputLayout) findViewById(R.id.et_first_name);
        etLastName = (TextInputLayout) findViewById(R.id.et_last_name);
        btnCreate = (Button) findViewById(R.id.btn_create);

        btnCreate.setOnClickListener(this);

        evtClient = new EVTApiClient(getString(R.string.api_key));
    }

    @Override
    public void onClick(View v) {
        String email = etEmail.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        String firstName = etFirstName.getEditText().getText().toString();
        String lastName = etLastName.getEditText().getText().toString();

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        progressDialog = ProgressDialog.show(this, "Creating user", "Sending details to EVT Server");
        evtClient.auth().createUser(user).execute(mCreateUserCallback);

    }

    private ServiceCallback<User> mCreateUserCallback = new ServiceCallback<User>() {
        @Override
        public void onResponse(User user) {
            progressDialog.dismiss();
            progressDialog = ProgressDialog.show(CreateUserActivity.this, "Creating user", "Sending details to EVT Server");
            evtClient.auth().validateUser(user.getUserId(), user.getActivationCode()).execute(mActivateUserCallback);
        }

        @Override
        public void onFailure(APIError e) {
            progressDialog.dismiss();
            progressDialog.dismiss();
            showError(e);
        }
    };

    private ServiceCallback<User> mActivateUserCallback = new ServiceCallback<User>() {

        @Override
        public void onResponse(User response) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateUserActivity.this);
            builder.setTitle("Request was successful");
            builder.setMessage("You have successfully created a user. Click Login to proceed to the auth screen");
            builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        @Override
        public void onFailure(APIError e) {
            progressDialog.dismiss();
            progressDialog.dismiss();
            showError(e);
        }
    };

    private void showError(APIError e) {

        String title = "Unknown Error";
        String message = "Something unexpectedly went wrong.";
        if(e != null) {
            title = e.getType() == APIError.Type.REQUEST ? "Request Failed" : "An Exception occurred";
            String errors = "";
            for(String error : e.getErrors()) {
                errors += error + "\n";
            }
            if(!TextUtils.isEmpty(errors)) {
                message = String.format("%s",errors);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateUserActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.create().show();

    }

}
