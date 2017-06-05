package com.evrythng.android.sdk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by phillipcui on 6/2/17.
 */

public class User {

    @SerializedName("evrythngUser")
    @Expose
    private String userId;

    @SerializedName("evrythngApiKey")
    @Expose
    private String apiKey;

    @Expose
    private String email;

    @Expose
    private String status;

    @Expose
    private String socialNetwork;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(String socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s", userId, apiKey, email, status);
    }
}
