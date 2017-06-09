package com.evrythng.android.sdk.wrapper.core;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phillipcui on 6/1/17.
 */

public class APIException extends IllegalStateException {

    private final APIError error;

    public APIException(@NonNull APIError error) {
        if(error == null)
            error = new APIError();
        this.error = error;
    }

    public List<String> getErrors() {
        return error.getErrors();
    }

    public int getStatus() {
        return error.getStatus();
    }

    public String getMoreInfo() {
        return error.getMoreInfo();
    }
}
