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

}
