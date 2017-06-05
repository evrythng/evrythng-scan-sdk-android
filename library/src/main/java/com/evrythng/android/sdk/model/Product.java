package com.evrythng.android.sdk.model;

import android.os.Parcel;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by phillipcui on 5/29/17.
 */

public class Product extends BaseModel {

    @Expose
    private String brand;

    @Expose
    private List<String> categories;

    @Expose
    private List<String> photos;

    @Expose
    private String url;

}
