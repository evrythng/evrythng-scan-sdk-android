package com.evrythng.android.sdk.model;

import com.google.gson.annotations.Expose;
import java.util.List;
import java.util.Map;

/**
 * Base class for all the resource models
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
    private Long createdAt;

    @Expose
    private Long updatedAt;

    @Expose
    private Long activatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = Long.valueOf(createdAt);
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = Long.valueOf(updatedAt);
    }

    public Long getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(long activatedAt) {
        this.activatedAt = Long.valueOf(activatedAt);
    }
}
