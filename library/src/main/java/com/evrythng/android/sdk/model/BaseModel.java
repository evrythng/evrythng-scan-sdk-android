package com.evrythng.android.sdk.model;

import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.Map;

/**
 * Created by phillipcui on 6/1/17.
 */

public class BaseModel {

    @Expose
    private String id;

    @Expose
    private String name;

    @Expose
    private String description;

    @Expose
    private Map<String, String> identifiers;

    @Expose
    private Map<String, String> properties;

    @Expose
    private List<String> tags;

    @Expose
    private Map<String,String> customFields;

    @Expose
    private long createdAt;

    @Expose
    private long updatedAt;

    @Expose
    private long activatedAt;

}
