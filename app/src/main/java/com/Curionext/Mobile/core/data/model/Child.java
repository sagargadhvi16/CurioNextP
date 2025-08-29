package com.curionext.mobile.core.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Child {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("age")
    private int age;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    @SerializedName("parent_id")
    private String parentId;

    @SerializedName("device_id")
    private String deviceId;

    @SerializedName("is_active")
    private boolean isActive;

    // Constructors
    public Child() {}

    public Child(String id, String name, int age, String avatarUrl, String parentId) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.avatarUrl = avatarUrl;
        this.parentId = parentId;
        this.isActive = true;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
