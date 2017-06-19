package com.evrythng.android.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by phillipcui on 5/29/17.
 */

public class Thng extends BaseModel {

    @Expose
    private String product;

    @Expose
    private JSONObject location;

    @Expose
    private List<String> collections;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public JSONObject getLocation() {
        return location;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public List<String> getCollections() {
        return collections;
    }

    public void setCollections(List<String> collections) {
        this.collections = collections;
    }
}
