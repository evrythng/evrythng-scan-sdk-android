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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
