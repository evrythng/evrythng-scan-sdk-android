package com.evrythng.android.sdk.model;

import com.google.gson.annotations.Expose;

/**
 * Model for the user credentials
 */

public class Credentials {

    @Expose
    private String email;

    @Expose
    private String password;

    @Expose
    private Boolean anonymous;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = Boolean.valueOf(anonymous);
    }
}
