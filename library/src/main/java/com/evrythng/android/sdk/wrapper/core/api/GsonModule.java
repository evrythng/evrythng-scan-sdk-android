package com.evrythng.android.sdk.wrapper.core.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by phillipcui on 6/2/17.
 */

public class GsonModule {

    private Gson gson;
    private static Object mutex = new Object();
    private static GsonModule ourInstance = new GsonModule();

    public static GsonModule getInstance() {
        if(ourInstance == null) {
            synchronized (mutex) {
                if (ourInstance == null) ourInstance = new GsonModule();
            }
        }
        return ourInstance;
    }

    private GsonModule() {

        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public Gson getGson() {
        return gson;
    }

}
