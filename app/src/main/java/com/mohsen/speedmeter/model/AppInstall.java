package com.mohsen.speedmeter.model;
import com.google.gson.Gson;

public class AppInstall {


    private String appName;
    private String channel;
    private String androidId;
    private String installationId;
    private String mobile;
    private String type;
    transient public UserInfo userInfo = new UserInfo();
    private String user_info;

    public void initUserInfo() {
        Gson gson = new Gson();
        user_info = gson.toJson(userInfo);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
