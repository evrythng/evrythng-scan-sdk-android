package com.evrythng.android.sdk.wrapper.core;

import com.google.gson.annotations.Expose;

import java.util.List;

import retrofit2.Response;

/**
 * Created by phillipcui on 6/1/17.
 */

public class APIError {

    public enum Type {
        EXCEPTION,
        REQUEST
    }

    private Type type;

    @Expose
    private int status;

    @Expose
    private List<String> errors;

    @Expose
    private int code;

    @Expose
    private String moreInfo;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
