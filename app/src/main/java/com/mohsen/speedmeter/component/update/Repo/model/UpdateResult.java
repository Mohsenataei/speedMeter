
package com.mohsen.speedmeter.component.update.Repo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UpdateResult implements Serializable {
    @SerializedName("version_code")
    @Expose
    private int versionCode;
    @SerializedName("version_name")
    @Expose
    private String versionName;
    @SerializedName("update_type")
    @Expose
    private int updateType;
    @SerializedName("download_link")
    @Expose
    private String downloadLink;
    @SerializedName("description")
    @Expose
    private String description;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
