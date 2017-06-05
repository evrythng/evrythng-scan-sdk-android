package com.evrythng.android.sdk.wrapper.util;

import android.support.annotation.NonNull;

import com.evrythng.android.sdk.wrapper.core.api.ServiceGenerator;
import com.evrythng.android.sdk.wrapper.core.APIError;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by phillipcui on 6/1/17.
 */

public class ErrorUtil {

    public static APIError parseError(@NonNull ServiceGenerator manager, Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                manager.getRetrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;
        try {
            error = converter.convert(response.errorBody());
            error.setType(APIError.Type.REQUEST);
        } catch (IOException e) {
            error = new APIError();
            error.setType(APIError.Type.EXCEPTION);
            String cause = e.getCause().getMessage();
            List<String> errors = new ArrayList<>();
            errors.add(cause == null ? "Exception on converting error response" : cause);
            error.setErrors(errors);
            return error;
        }
        return error;
    }

    public static APIError parseException(Throwable t) {
        APIError error = new APIError();
        error.setType(APIError.Type.EXCEPTION);
        List<String> errors = new ArrayList<>();

        if(t instanceof IOException) {
            errors.add("No network connection");
        }
        else if(t instanceof SocketTimeoutException) {
            errors.add("Request timeout");
        }
        else {
            errors.add("Unexpected Error");
        }
        return error;
    }
}
